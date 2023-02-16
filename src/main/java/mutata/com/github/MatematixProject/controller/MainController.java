package mutata.com.github.MatematixProject.controller;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.entity.*;
import mutata.com.github.MatematixProject.entity.dto.CommentDTO;
import mutata.com.github.MatematixProject.service.*;
import mutata.com.github.MatematixProject.util.*;
import mutata.com.github.MatematixProject.util.mail.JavaMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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

    private final CommentActionService commentActionService;
    private final MyRestService restService;
    @Autowired
    public MainController(UserService userService, JavaMailServiceImpl javaMailSenderImpl, AvatarInfoService avatarInfoService,
                          CommentService commentService, UserUtils userUtils, SimpMessagingTemplate template,CommentActionService commentActionService,MyRestService restService) {
        this.userService = userService;
        this.javaMailSenderImpl = javaMailSenderImpl;
        this.avatarInfoService = avatarInfoService;
        this.commentService = commentService;
        this.userUtils = userUtils;
        this.messagingTemplate = template;
        this.commentActionService = commentActionService;
        this.restService = restService;
    }

    @GetMapping(value = {"/","/index"})
    public String showIndexPage(@CookieValue(value = "jax",defaultValue = "false") String seenJaxToast,Model model) {
        if("false".equalsIgnoreCase(seenJaxToast)) {
            model.addAttribute("seenJaxToast", false);
            model.addAttribute("loadToastr",true);
        }
        else
            model.addAttribute("seenJaxToast", true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.isAuthenticated()) {
            userUtils.loadAvatar(auth.getName(),model,"");
            model.addAttribute("username",auth.getName());
        }
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
                                                  @RequestParam(required = false) String subject,@RequestParam(required = false) String message,Model model) {
        boolean hasErrors = name == null || subject == null || email == null || message == null || !email.contains("@");
        if (hasErrors) {
            StringBuilder result = new StringBuilder();
            if (name == null)
                result.append("Имя не должно быть пустым|");
            if (subject == null)
                result.append("Тема не должна быть пустой|");
            if (email == null)
                result.append("Почта не должна быть пустой|");
            if (message == null)
                result.append("Сообщение не должно быть пустым|");
            if (email != null && !email.contains("@"))
                result.append("Неправильный формат почты|");
            return result.toString();
        }
        String messageTemplate = String.format("%s(%s) - %s:\n%s", name, email, subject, message);
        javaMailSenderImpl.send("matematixtest@gmail.com", "MatematiX - Свяжитесь с нами", messageTemplate);
        javaMailSenderImpl.send(email, "MatematiX - Свяжитесь с нами", "Ваше сообщение было успешно доставлено.Спасибо за оставленный отзыв.");
        return "success";
    }
    @GetMapping("/books")
    public String showBooksPage() {
        return "books";
    }

    @GetMapping("/articles/{article}")
    public String showArticle(@PathVariable String article,Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.isAuthenticated()) {
            userUtils.loadAvatar(auth.getName(),model,"");
            model.addAttribute("username",auth.getName());
        }
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

    class Container<T,R> {
        T t;
        R r;
        Container(T t,R r) {
            this.t = t;
            this.r = r;
        }

        public T getT() {
            return t;
        }
        public R getR() {
            return r;
        }
    }

    //FIXME Сделать сортировку комментариев;
    private Container<List<Comment>,HashMap<String,AvatarInfo>> initializeJSCL(User receiver,int currentPage,int count,Model model, List<String> toExclude) { // Java Script Comment List
        Page<Comment> thePage = commentService.findAllReturnPage(receiver,currentPage-1,count);
        List<Comment> comments = Utils.reversedView(thePage.getContent());
        HashMap<String,AvatarInfo> usernameToAvatarInfo = new HashMap<>(12);
        HashMap<String,Boolean> isUserAlive = new HashMap<>(12);
        for (Comment comment : comments) {
            String name = comment.getAuthor();
            comment.setStringDate(Utils.formatDate(comment.getDate()));
            if(toExclude == null || !toExclude.contains(comment.getAuthor())) {
                var avatarInfo = avatarInfoService.findByName(name).orElse(null);
                usernameToAvatarInfo.putIfAbsent(name,avatarInfo);
                isUserAlive.putIfAbsent(name,userUtils.isAlive(name));
                if(avatarInfo != null)
                  avatarInfo.setEncodedAvatar(Utils.encodeAvatar(avatarInfo.getAvatar()));
            }
        }
       if(model != null) {
           model.addAttribute("isUserAlive",isUserAlive);
           model.addAttribute("totalPages",thePage.getTotalPages());
       }
        return new Container<>(comments,usernameToAvatarInfo);
    }

    @GetMapping(value = {"/profile/{username}","/profile"})
    public String showProfilePage(@PathVariable(required = false) String username,Model model) {
        if(username == null)
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        username = StringUtils.capitalize(username.toLowerCase());
        User user = userService.findByNameIgnoreCase(username);
        if (user == null) {
            return "error/404";
        }
        MyResponse<User> friends = userService.findAllFriendsReturnPage(user,0,6);
        long countOfFriends = friends.getTotal();
        model.addAttribute("countOfFriends",countOfFriends);
        HashMap<String,AvatarInfo> avatars = new HashMap<>();
        if(countOfFriends != 0) {
            friends.getContent().forEach(theUser -> theUser.storeAvatar(avatars));
            model.addAttribute("friends",friends.getContent().stream().map(User::getName).toList());
        }

        model.addAttribute("rating",user.getRating());
        model.addAttribute("totalPages",(int) Math.ceil(user.getComments().size() / (8 * 1.0)));
        model.addAttribute("name",user.getName());
        model.addAttribute("lvl","ROLE_ADMIN".equalsIgnoreCase(user.getRole()) ? "Администратор" : "Пользователь");
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("active",userUtils.isAlive(username));
        userUtils.loadAvatar(currentUserName,avatars,model,"my");
        userUtils.loadAvatar(username,avatars,model,"");
        model.addAttribute("currentUserName",currentUserName);
        model.addAttribute("avatars",avatars);
        model.addAttribute("myProfile",currentUserName.equalsIgnoreCase(username));
        return "profile/index";
    }

    @PostMapping(value = "/uploadAvatar",consumes = {"multipart/form-data"})
    public String uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Double x, @RequestParam(required = false) Double y,
                               @RequestParam(required = false) Double width, @RequestParam(required = false) Double height, @RequestParam (required = false)Double scaleX,
                               @RequestParam(required = false) Double scaleY) {


        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(file.isEmpty() || x == null || y == null || width == null || scaleX == null || scaleY == null || height == null) {
            return "redirect:/profile/settings?emptyFile=true";
        }
        try {
            BufferedImage result = Utils.cropAnImage(x,y,scaleX,scaleY,ImageIO.read(file.getInputStream()),width,height);
            AvatarInfo theInfo = avatarInfoService.findByName(name).orElse(null);
            if(theInfo == null) {
                theInfo = new AvatarInfo();
                theInfo.setUsername(name);
            }
            String format = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            JsonNode node = new ObjectMapper().readTree(restService.convertToWebp(format,Utils.encodeAvatar(Utils.convertBufferedImageToBytes(result,format))));
            theInfo.setAvatar(Utils.decodeAvatar(node.path("base64String").asText()));
            avatarInfoService.save(theInfo);
        }

        catch (IOException exception) {
            log.error(exception.toString());
        }

        return "redirect:profile/" + name;
    }
    @MessageMapping("/sendComment") // /application/sendComment
    public void send(CommentDTO commentDTO) {
        commentDTO.setRating(0);
        commentDTO.setStringDate(Utils.formatDate(new Date(commentDTO.getDate())));
        var temp = Utils.toComment(commentDTO,userService);
        commentService.save(temp);
        commentDTO.setId(temp.getId());
        messagingTemplate.convertAndSendToUser(commentDTO.getRecipient(),"/queue/messages",commentDTO);
    }

    /**
     * For SOME reasons I get an error when JACKSON tries to deserialize data from JS(JSON.stringify({"id":id,"username":username,"like": true})) to two Strings (public void plusLike(String username,String id,boolean like) {...})
     * So I came up with this temporary solution until I find out what's going on.
     **/
    @Data
    static class LikeDTO {
        public String username;
        public String id;
        public boolean like;
        public LikeDTO() {

        }
    }

    @MessageMapping("/changeLikeAmount") //
    public void changeLikeAmount(LikeDTO data) {
        String username = data.username;
        Long parsedId = Long.parseLong(data.id);
        Comment comment = commentService.findById(parsedId);
        CommentAction action = commentActionService.findAction(parsedId,username);
        if(action == null)
            action = new CommentAction(parsedId,username, (byte)-1);
        Byte theAction = action.getAction();
        boolean isLike = data.isLike();
        if(isLike)
            comment.setRating(comment.getRating() + (theAction == -1 ? 1 : theAction == 0 ? 2 : -1));
        else
            comment.setRating(comment.getRating() + (theAction == -1 ? -1 : theAction == 1 ? -2 : 1));
        commentService.save(comment);
        action.setAction((byte) (isLike ?  1 : 0));
        if(isLike) {
            if (theAction == 1)
                commentActionService.delete(action);
            else
                commentActionService.save(action);
        } else {
            if(theAction == 0)
                commentActionService.delete(action);
            else
                commentActionService.save(action);
        }

        messagingTemplate.convertAndSendToUser(comment.getReceiver().getName(),"/queue/changeLikeAmount",parsedId + " " + comment.getRating());
    }

    @GetMapping("/ajax/nextComments")
    public @ResponseBody Container<List<Comment>, HashMap<String, AvatarInfo>> getNextComments(String user, int currentPage,String usersWhoseAvatarsAreLoaded) {
        currentPage = Math.max(currentPage, 1);
        User receiver = userService.findByNameIgnoreCase(user);
        String[] array = usersWhoseAvatarsAreLoaded.split(",");
        // Контейнер содержит комментарии и аватарки тех пользователей, которые еще не загружены.
        var temp = initializeJSCL(receiver,currentPage,8,null,Arrays.asList(array));
        temp.t = Utils.reversedView(temp.t);
        return temp;
    }

    @GetMapping("/ajax/nextFriends")
    public @ResponseBody List<AvatarInfo> getNextFriends(String user, int currentPage,String usersWhoseAvatarsAreLoaded) {
        currentPage = Math.max(currentPage, 1);
        List<String> array = Arrays.asList(usersWhoseAvatarsAreLoaded.split(","));
        var friends = userService.findAllFriendsReturnPage(userService.findByNameIgnoreCase(user),currentPage,6).getContent();
        List<AvatarInfo> infos = new ArrayList<>();
        for(User tempUser : friends) {
            if(!array.contains(tempUser.getName()))
                infos.add(avatarInfoService.findByNameOrReturnEmpty(tempUser.getName()));
        }
        return infos;
    }
    @GetMapping("/ajax/isUserAlive")
    public @ResponseBody Boolean isUserAlive(String username) {
        return userUtils.isAlive(username);
    }
    @GetMapping("/settings")
    public static String showSettingsPage() {
        return "profile/settings";
    }
}
