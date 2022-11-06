package mutata.com.github.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import mutata.com.github.entity.Article;
import mutata.com.github.entity.AvatarInfo;
import mutata.com.github.entity.Comment;
import mutata.com.github.entity.User;
import mutata.com.github.entity.dto.MessageDTO;
import mutata.com.github.service.AvatarInfoService;
import mutata.com.github.service.CommentService;
import mutata.com.github.service.UserService;
import mutata.com.github.util.JavaMailSenderWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.awt.image.BufferedImage;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
@Slf4j  // Simple Logging Facade for Java
@RequestMapping
public class MainController {

    private final UserService userService;
    private final JavaMailSenderWrapper javaMailSenderWrapper;

    private final AvatarInfoService avatarInfoService;
    private final CommentService commentService;

    @Autowired
    public MainController(UserService userService, JavaMailSenderWrapper javaMailSenderWrapper,AvatarInfoService avatarInfoService,
                          CommentService commentService) {
        this.userService = userService;
        this.javaMailSenderWrapper = javaMailSenderWrapper;
        this.avatarInfoService = avatarInfoService;
        this.commentService = commentService;
    }

    @GetMapping(value = {"/","/index"})
    public String showIndexPage(@CookieValue(value = "jax",defaultValue = "false") String seenJaxToast,Model model) {
        if("false".equalsIgnoreCase(seenJaxToast))
            model.addAttribute("seenJaxToast",false);
        else
            model.addAttribute("seenJaxToast",true);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.isAuthenticated()) {
            AvatarInfo info = avatarInfoService.findByName(auth.getName()).orElse(null);

            if(info == null)
                model.addAttribute("hasAvatar",false);
            else {
                model.addAttribute("hasAvatar",true);
                model.addAttribute("avatar",Base64.getEncoder().withoutPadding().encodeToString(info.getAvatar()));
                model.addAttribute("avatarFormat",info.getAvatarFormat());
            }
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
    public String showGetInTouchPage(@ModelAttribute MessageDTO messageDTO) {
        return "getInTouch";
    }
    @PostMapping("/process_getInTouch")
    public String processGetInTouch(@ModelAttribute @Valid MessageDTO messageDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "getInTouch";
        }
        String message = String.format("%s(%s) - %s:\n%s",messageDTO.getName(),messageDTO.getEmail(),messageDTO.getSubject(),messageDTO.getMessage());

         javaMailSenderWrapper.send("matematixtest@gmail.com","MatematiX - Свяжитесь с нами",message);

        return "getInTouchResult";
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

    @Data
    private static class JavaScriptComment {
        private String avatar;
        private String username;
        private long rating;
        private Date date;
        private String content;
        private String format;
    }
    // FIXME NOT OPTIMIZED
    private List<JavaScriptComment> initializeJSCL(User receiver) { // Java Script Comment List
        List<JavaScriptComment> list = new ArrayList<>();
        List<Comment> comments = commentService.findByReceiver(receiver);
        for (Comment comment : comments) {
            JavaScriptComment jsc = new JavaScriptComment();
            jsc.setContent(comment.getContent());
            jsc.setRating(comment.getRating());
            jsc.setUsername(comment.getAuthor());
            jsc.setDate(comment.getDate());
            String actualData = null;
            Optional<AvatarInfo> data = avatarInfoService.findByName(comment.getAuthor());
            if (data.isPresent()) {
                actualData = Base64.getEncoder().withoutPadding().encodeToString(data.get().getAvatar());
                jsc.setFormat(data.get().getAvatarFormat());
            }
            jsc.setAvatar(actualData);
            list.add(jsc);
        }
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

        List<JavaScriptComment> commentsOnTheWall = initializeJSCL(user); // JavaScriptCommentsList
        model.addAttribute("hasComments",commentsOnTheWall.size() != 0);
        model.addAttribute("name",user.getName());
        model.addAttribute("lvl","ROLE_ADMIN".equalsIgnoreCase(user.getRole()) ? "Администратор" : "Пользователь");
        model.addAttribute("commentsOnTheWall",commentsOnTheWall);
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("canChangeAvatar",currentUserName.equalsIgnoreCase(username));
        AvatarInfo info = avatarInfoService.findByName(username).orElse(null);
        AvatarInfo currentUserInfo = avatarInfoService.findByName(currentUserName).orElse(null);
        model.addAttribute("currentUserName",currentUserName);
        if(currentUserInfo == null)
            model.addAttribute("hasMyAvatar", false);
        else {
            model.addAttribute("myAvatar", Base64.getEncoder().withoutPadding().encodeToString(currentUserInfo.getAvatar()));
            model.addAttribute("hasMyAvatar", true);
            model.addAttribute("myAvatarFormat",currentUserInfo.getAvatarFormat());
        }
        if(info == null)
            model.addAttribute("hasAvatar",false);
        else {
            model.addAttribute("hasAvatar",true);
            model.addAttribute("avatar",Base64.getEncoder().withoutPadding().encodeToString(info.getAvatar()));
            model.addAttribute("avatarFormat",info.getAvatarFormat());
        }
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
            BufferedImage result = cropAnImage(x,y,scaleX,scaleY,ImageIO.read(file.getInputStream()),width,height);
            AvatarInfo theInfo = avatarInfoService.findByName(name).orElse(null);
            if(theInfo == null) {
                theInfo = new AvatarInfo();
                theInfo.setUsername(name);
            }
            theInfo.setAvatarFormat(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
            theInfo.setAvatar(convertBufferedImageToBytes(result,theInfo.getAvatarFormat()));
            avatarInfoService.save(theInfo);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return "redirect:profile/" + name;
    }

    private byte[] convertBufferedImageToBytes(BufferedImage image,String format) {
        byte[] bytes = null;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            bytes = baos.toByteArray();
        }  catch (IOException exception) {
            exception.printStackTrace();
        }
        return bytes;
    }

    private BufferedImage cropAnImage(double x,double y,double scaleX,double scaleY,BufferedImage src, double width,double height) {
        return src.getSubimage((int) (x * scaleX),(int) (y * scaleY),(int) width,(int) height);
    }
    @PostMapping(value = "createComment", produces = "text/html; charset=utf-8")
    public String postComment(@RequestParam String content,@RequestParam(required = false) String receiver) {
        User userReceiver = userService.findByNameIgnoreCase(receiver);
        content = encodeFromIsoToUTF8(content);
        System.out.println(content);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setDate(new Date());
        comment.setRating(0);
        comment.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
        comment.setReceiver(userReceiver);
        commentService.save(comment);
        return "redirect:profile/" + receiver;
    }

    // Since the standard encoding is ISO and not UTF for SOME MYSTERIOUS REASONS( though I set everything up)
    private String encodeFromIsoToUTF8(String content) {
        return new String(content.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

}
