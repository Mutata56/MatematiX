package mutata.com.github.MatematixProject.controller;


import lombok.extern.slf4j.Slf4j;
import mutata.com.github.MatematixProject.entity.Article;
import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.service.AvatarInfoService;
import mutata.com.github.MatematixProject.service.CommentService;
import mutata.com.github.MatematixProject.service.UserService;
import mutata.com.github.MatematixProject.util.JavaMailServiceImpl;
import mutata.com.github.MatematixProject.util.Message;
import mutata.com.github.MatematixProject.util.UserUtils;
import mutata.com.github.MatematixProject.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@Controller
@Slf4j  // Simple Logging Facade for Java
@RequestMapping
public class MainController {

    private final UserService userService;
    private final JavaMailServiceImpl javaMailSenderImpl;

    private final AvatarInfoService avatarInfoService;
    private final CommentService commentService;

    private final SimpMessagingTemplate messagingTemplate;
    private final UserUtils userUtils;

    @Autowired
    public MainController(UserService userService, JavaMailServiceImpl javaMailSenderImpl, AvatarInfoService avatarInfoService,
                          CommentService commentService, UserUtils userUtils, SimpMessagingTemplate template) {
        this.userService = userService;
        this.javaMailSenderImpl = javaMailSenderImpl;
        this.avatarInfoService = avatarInfoService;
        this.commentService = commentService;
        this.userUtils = userUtils;
        this.messagingTemplate = template;
    }

    @GetMapping(value = {"/","/index"})
    public String showIndexPage(@CookieValue(value = "jax",defaultValue = "false") String seenJaxToast,Model model) {
        if("false".equalsIgnoreCase(seenJaxToast))
            model.addAttribute("seenJaxToast",false);
        else
            model.addAttribute("seenJaxToast",true);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.isAuthenticated())
            userUtils.hasAvatar(auth.getName(),model);
        return "index";
    }
    @GetMapping("/bookmarks")
    public String showBookmarksPage(Model model) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Article> articles = userService.findByNameIgnoreCaseAndLoadArticles(currentUser).getArticles();
        model.addAttribute("articles",articles);
        return "bookmarks";
    }
    @GetMapping("/getInTouch")
    public String showGetInTouchPage() {
        return "getInTouch";
    }
    @PostMapping("/process_getInTouch")
    public @ResponseBody String processGetInTouch(@RequestParam(required = false) String name,@RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String subject,@RequestParam(required = false) String message) {
        if(name == null || subject == null || email == null || message == null || !email.contains("@"))
            return "error";

        String messageTemplate = String.format("%s(%s) - %s:\n%s",name,email,subject,message);
        javaMailSenderImpl.send("matematixtest@gmail.com","MatematiX - Свяжитесь с нами",messageTemplate);

        return "success";
    }
    @GetMapping("/books")
    public String showBooksPage() {
        return "books";
    }

    @GetMapping("/articles/{article}")
    public String showArticle(@PathVariable String article) {
        return String.format("articles/%s",article);
    }

    @GetMapping("/jax")
    public String showJaxPage() {
        return "jax";
    }
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor =  new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private static List<Comment> reversedView(final List<Comment> list)
    {
        return new AbstractList<>()
        {
            @Override
            public Comment get(int index)
            {
                return list.get(list.size()-1-index);
            }

            @Override
            public int size()
            {
                return list.size();
            }
        };
    }

    // FIXME NOT OPTIMIZED;Сделать сортировку комментариев
    private List<Message> initializeJSCL(User receiver,int currentPage,int pages,Model model) { // Java Script Comment List
        List<Message> list = new ArrayList<>();
        Page<Comment> thePage = commentService.findAllReturnPage(receiver,currentPage-1,pages);
        List<Comment> comments = reversedView(thePage.getContent());

        if(model != null)
            model.addAttribute("totalPages",thePage.getTotalPages());
        for (Comment comment : comments) {
            Message jsc = new Message();
            jsc.setContent(comment.getContent());
            jsc.setRating(comment.getRating());
            jsc.setUsername(comment.getAuthor());
            jsc.setDate(Utils.formatDate(comment.getDate()));
            String actualData = null;
            Optional<AvatarInfo> data = avatarInfoService.findByName(comment.getAuthor());
            if (data.isPresent()) {
                actualData = Utils.encodeAvatar(data.get().getAvatar());
                jsc.setFormat(data.get().getAvatarFormat());
                jsc.setAvatar(actualData);
            }
            list.add(jsc);
        }
        HashMap<String,Boolean> isUserAlive = new HashMap<>();
        list.forEach(element -> {
            if(!isUserAlive.containsKey(element.getUsername())) {
                isUserAlive.put(element.getUsername(),userUtils.isAlive(element.getUsername()));
            }
        });
        list.forEach(element -> element.setIsActive(isUserAlive.get(element.getUsername())));
        return list;
    }

    @GetMapping(value = {"/profile/{username}","/profile"})
    public String showProfilePage(@PathVariable(required = false) String username,Model model) {
        if(username == null)
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByNameIgnoreCase(username);

        if (user == null) {
            return "404";
            //FIXME 404 ERROR PAGE
        }
        model.addAttribute("rating",user.getRating());
        List<Message> commentsOnTheWall = initializeJSCL(user,1,3,model); // JavaScriptCommentsList
        Collections.reverse(commentsOnTheWall);
        model.addAttribute("hasComments",commentsOnTheWall.size() != 0);
        model.addAttribute("name",user.getName());
        model.addAttribute("lvl","ROLE_ADMIN".equalsIgnoreCase(user.getRole()) ? "Администратор" : "Пользователь");
        model.addAttribute("commentsOnTheWall",commentsOnTheWall);
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("canChangeAvatar",currentUserName.equalsIgnoreCase(username));

        model.addAttribute("currentUserName",currentUserName);
        model.addAttribute("active",userUtils.isAlive(username));
        userUtils.hasAvatar(currentUserName,model,"my");
        userUtils.hasAvatar(username,model);
        return "profile/index";
    }

    @PostMapping(value = "/uploadAvatar",consumes = {"multipart/form-data"})
    public String uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam double x, @RequestParam double y,
                               @RequestParam double width, @RequestParam double height, @RequestParam double scaleX,
                               @RequestParam double scaleY) {
        if(file.isEmpty()) {
            //FIXME HANDLE
            return "/profile";
        }

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            BufferedImage result = Utils.cropAnImage(x,y,scaleX,scaleY,ImageIO.read(file.getInputStream()),width,height);
            AvatarInfo theInfo = avatarInfoService.findByName(name).orElse(null);
            if(theInfo == null) {
                theInfo = new AvatarInfo();
                theInfo.setUsername(name);
            }
            theInfo.setAvatarFormat(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
            theInfo.setAvatar(Utils.convertBufferedImageToBytes(result,theInfo.getAvatarFormat()));
            avatarInfoService.save(theInfo);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return "redirect:profile/" + name;
    }

    private void postComment(Message message) {
        Comment comment = new Comment();
        comment.setContent(message.getContent());
        try {
            comment.setDate(Utils.parseDate(message.getDate()));
        } catch (Exception exception) {
            log.error(exception.toString());
            comment.setDate(new Date());
        }
        comment.setRating(0);
        comment.setAuthor(message.getUsername());
        comment.setReceiver(userService.findByNameIgnoreCase(message.getRecipient()));
        commentService.save(comment);
    }
    @MessageMapping("/sendComment") // /application/sendComment
    public void send(Message message) {
        message.setRating(0);
        AvatarInfo info = avatarInfoService.findByName(message.getUsername()).orElse(null);
        if(info != null) {
            message.setFormat(info.getAvatarFormat());
            message.setAvatar(Utils.encodeAvatar(info.getAvatar()));
        }
        message.setDate(Utils.formatDate(new Date()));
        postComment(message);
        messagingTemplate.convertAndSendToUser(message.getRecipient(),"/queue/messages",message);
    }
    @GetMapping("/ajax/nextComments")
    public @ResponseBody List<Message> getNextComments(String user,int currentPage) {
        User receiver = userService.findByNameIgnoreCase(user);
        return initializeJSCL(receiver,currentPage,3,null);
    }
}
