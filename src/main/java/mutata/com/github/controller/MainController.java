package mutata.com.github.controller;
import lombok.extern.slf4j.Slf4j;
import mutata.com.github.entity.Article;
import mutata.com.github.entity.User;
import mutata.com.github.entity.dto.MessageDTO;
import mutata.com.github.service.UserService;
import mutata.com.github.util.JavaMailSenderWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j  // Simple Logging Facade for Java
@RequestMapping
public class MainController {

    private final UserService userService;
    private final JavaMailSenderWrapper javaMailSenderWrapper;
    @Autowired
    public MainController(UserService userService,JavaMailSenderWrapper javaMailSenderWrapper) {
        this.userService = userService;
        this.javaMailSenderWrapper = javaMailSenderWrapper;
    }

    @GetMapping
    public String showIndexPage() {
        System.out.println("Current user: " + SecurityContextHolder.getContext().getAuthentication().getName());
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

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor =  new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
}
