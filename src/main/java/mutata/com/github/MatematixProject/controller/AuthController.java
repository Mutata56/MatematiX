package mutata.com.github.MatematixProject.controller;

import lombok.extern.slf4j.Slf4j;
import mutata.com.github.MatematixProject.entity.*;
import mutata.com.github.MatematixProject.entity.dto.PasswordDTO;
import mutata.com.github.MatematixProject.entity.dto.RegisterDTO;
import mutata.com.github.MatematixProject.entity.token.Token;
import mutata.com.github.MatematixProject.entity.token.TokenType;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.event.OnResetPasswordEvent;
import mutata.com.github.MatematixProject.service.ResetPasswordTokenService;
import mutata.com.github.MatematixProject.service.TokenService;
import mutata.com.github.MatematixProject.service.UserService;
import mutata.com.github.MatematixProject.service.VerificationTokenService;
import mutata.com.github.MatematixProject.util.RegisterValidator;
import mutata.com.github.MatematixProject.util.Toastr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@Slf4j // Simple Logging Facade for Java
@RequestMapping("/auth")
public class AuthController {

    private final ApplicationEventPublisher publisher;

    private final RegisterValidator registerValidator;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;




    @Autowired
    public AuthController(UserService userService, VerificationTokenService verificationTokenService, ApplicationEventPublisher publisher,
                          RegisterValidator registerValidator, ResetPasswordTokenService resetPasswordTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.publisher = publisher;
        this.registerValidator = registerValidator;
        this.resetPasswordTokenService = resetPasswordTokenService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "authorization/login";
    }
    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "authorization/forgotPassword";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("dto",new RegisterDTO());
        return "authorization/register";
    }
    @PostMapping("/register")
    public String registerUser(HttpServletRequest request, @Valid @ModelAttribute(name = "dto",binding = true) RegisterDTO registerDTO,
                               BindingResult result,Model model) {
        registerValidator.validate(registerDTO,result);
        if(result.hasErrors()) {
            Toastr.addErrorsToModel(model,result);
            return "/authorization/register";
        }

        User user = new User();
        user.setEncryptedPassword(registerDTO.getPassword());
        user.setEmail(StringUtils.capitalize(registerDTO.getEmail().toLowerCase()));
        user.setRole("ROLE_USER");
        user.setName(StringUtils.capitalize(registerDTO.getName().toLowerCase()));

        userService.save(user);
        publisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getContextPath()));
        try {
            request.login(user.getName(),registerDTO.getPassword());
        } catch (ServletException exception) {
            log.info("Error while login " + exception);
        }
        return "redirect:/";
    }


    @GetMapping("/registrationConfirm")
    public String registrationConfirm(@RequestParam("token") String token,Model model,HttpServletRequest request) {
        return processTokens(token,verificationTokenService,model, TokenType.VERIFICATION,request);
    }
    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam("token") String token,Model model,HttpServletRequest request) {
        return processTokens(token,resetPasswordTokenService,model,TokenType.RESET,request);
    }
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute PasswordDTO passwordDTO, BindingResult result,
                                Model model) {
        String tokenString = passwordDTO.getToken();
        boolean hasErrors = false;
        if(result.hasErrors()) {
            model.addAttribute("token",tokenString);
            model.addAttribute("passwordDTO",passwordDTO);
            Toastr.addErrorsToModel(model,result);
            hasErrors = true;
        }
        if(passwordDTO.getPassword() == null || !passwordDTO.getPassword().equals(passwordDTO.getPasswordAgain())) {
            if(!hasErrors) {
                model.addAttribute("token",tokenString);
                model.addAttribute("passwordDTO",passwordDTO);
            }
            Toastr.addErrorToModel(model,"Пароли не совпадают");
            hasErrors = true;
        }
        if(hasErrors)
            return "authorization/resetPassword";
        Token token = resetPasswordTokenService.findByToken(tokenString);

        if(token == null ) {
            model.addAttribute("TokenNotFound",true);
            return "authorization/forgotPassword";
        }
        User userFromDB = resetPasswordTokenService.findByToken(tokenString).getUser();
        userFromDB.setEncryptedPassword(passwordDTO.getPassword());
        userService.save(userFromDB);
        resetPasswordTokenService.delete(tokenString);
        model.addAttribute("success",true);
        model.addAttribute("resetPassword",true);

        return "authorization/login";
    }

    private String processTokens(String tokenStr, TokenService tokenService, Model model, TokenType tokenType,HttpServletRequest request) {
        boolean isTokenTypeVerification = tokenType == TokenType.VERIFICATION;
        Token token = tokenService.findByToken(tokenStr);

        if(token == null ) {
            model.addAttribute("notFound",true);
            return "index";
        }

        User user = token.getUser();
        LocalDateTime now = LocalDateTime.now();
        if ((token.getExpirationDate().isBefore(now))) {
            tokenService.delete(tokenStr);
            model.addAttribute("expired",true);

            if(isTokenTypeVerification) {
                publisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getContextPath()));
            } else {
                publisher.publishEvent(new OnResetPasswordEvent(user,request.getContextPath()));
            }
            return "index";
        } else if(isTokenTypeVerification) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String usernameFromDataBase = token.getUser().getName();
            if(!username.equals(usernameFromDataBase)) {
                model.addAttribute("nonValidUser",true);
                return "index";
            }
        }

        if(isTokenTypeVerification) {
            user.setEnabled((byte) 1);
            userService.saveWithoutPasswordEncryption(user);
            tokenService.delete(tokenStr);
        } else if(tokenType == TokenType.RESET) {
            model.addAttribute("token",tokenStr);
            model.addAttribute("passwordDTO",new PasswordDTO());
            return "authorization/resetPassword";
        }

        model.addAttribute("successEmailConfirmation",true);

        return "index";
    }




    @PostMapping("/ajax/process_forgotPassword")
    public @ResponseBody boolean processForgotPassword(@RequestParam(name = "username",required = false) String username,HttpServletRequest request) {
        User user = null;
            if(username != null)
                if(username.contains("@"))
                    user = userService.findByEmailIgnoreCase(username);
                else
                    user = userService.findByNameIgnoreCase(username);

        if(user == null )
            return false;
        if(user.getResetPasswordToken() == null)
            publisher.publishEvent(new OnResetPasswordEvent(user,request.getContextPath()));
        return true;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor =  new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
    //FIXME ADD Change EMAIL OPTION
    @GetMapping("/ajax/isLoginAlreadyTaken")
    public @ResponseBody boolean isLoginAlreadyTakenAjax(@RequestParam String login) {
        return login == null || registerValidator.isNameAlreadyTaken(login);
    }
    @GetMapping("/ajax/isEmailAlreadyTaken")
    public @ResponseBody boolean isEmailAlreadyTakenAjax(@RequestParam String email) {
        return email == null || registerValidator.isEmailAlreadyTaken(email);
    }
    @GetMapping("/ajax/doPasswordsNotMatch")
    public @ResponseBody boolean doPasswordsNotMatchAjax(@RequestParam(required = false) String password,@RequestParam(required = false) String passwordAgain) {
        if(password == null || passwordAgain == null)
            return true;
        return !password.equals(passwordAgain);
    }

}
