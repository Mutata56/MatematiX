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
/**
 * Контроллер всего, что свзяано с ауентификацией.
 * @author Khaliullin Cyrill
 * @version 1.0.0
 *
 * Controller - данный класс является контроллером (предназначен для непосредственной обработки запросов от клиента и возвращения результатов).
 * RequestMapping - обрабытывает все запросы с префиксом /auth/...
 * Slf4j - Предоставляет интерфейс для логирования, направляя вызовы в конкретную реализацию логгера.
 */
@Controller
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    /**
     * Объект, предназначенный для создания событий (Конец регистрации, запрос смены пароля).
     */
    private final ApplicationEventPublisher publisher;
    /**
     * Валидатор для регистрации.
     */
    private final RegisterValidator registerValidator;
    /**
     * Объекты, предназначенные для работы с БД users,VerificationTokens,ResetPasswordTokens соответсвтенно.
     */
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

    /**
     * Метод, отвечающий за показ регистрационной страницы посредством GET запроса по адресу "/register".
     * RegisterDTO - data transfer object - объект, используемый для передачи регистрационных данных.
     * @param model - модель в контексте MVC.
     * @return JSP страницу по адресу "authorization/register".
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("dto",new RegisterDTO());
        return "authorization/register";
    }

    /**
     * Метод, отвечающий за регистрацию конечного пользователя в системе посредством POST запроса по адресу "/register".
     * @param request - HTTP запрос.
     * @param registerDTO - data transfer object - объект, используемый для передачи регистрационных данных.
     * @param result - результат проверки всех введённых данных с помощью валидатора.
     * @param model - модель в контексте MVC.
     * @return Если содержит ошибки - возвращаем на страницу регистрации, иначе перекидываем пользователя на начальный экран.
     */
    @PostMapping("/register")
    public String registerUser(HttpServletRequest request, @Valid @ModelAttribute(name = "dto",binding = true) RegisterDTO registerDTO,
                               BindingResult result,Model model) {
        registerValidator.validate(registerDTO,result); // Валидация введённых данных
        if(result.hasErrors()) {
            Toastr.addErrorsToModel(model,result); // Добавления ошибок в Toastr (Плагин, отвчеающий за уведомления)
            return "/authorization/register";
        }

        User user = new User();
        user.setEncryptedPassword(registerDTO.getPassword());
        user.setEmail(StringUtils.capitalize(registerDTO.getEmail().toLowerCase()));
        user.setRole("ROLE_USER");
        user.setName(StringUtils.capitalize(registerDTO.getName().toLowerCase()));

        userService.save(user);
        publisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getContextPath())); // Создание нового ивента (Завершение регистрации), нужно, чтобы подтвердить почту пользователя.
        try {
            request.login(user.getName(),registerDTO.getPassword()); // Авто логин для пользователя, чтобы не вводить данные повторно после регистрации
        } catch (ServletException exception) {
            log.info("Error while login " + exception);
        }
        return "redirect:/";
    }

    /**
     * Метод, отвечающий за подтверждение почты
     */
    @GetMapping("/registrationConfirm")
    public String registrationConfirm(@RequestParam("token") String token,Model model,HttpServletRequest request) {
        return processTokens(token,verificationTokenService,model, TokenType.VERIFICATION,request);
    }
    /**
     * Метод, отвечающий за показ страницы для смены пароля на новый посредством GET запроса по адресу "/resetPassword".
     * Страница показывается только в случае правильно переданного токена. существующего в БД.
     */
    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam("token") String token,Model model,HttpServletRequest request) {
        return processTokens(token,resetPasswordTokenService,model,TokenType.RESET,request);
    }

    /**
     * Метод, отвечающий за смену пароля посредством POST запроса по адресу "/resetPassword"
     * @param passwordDTO - data transfer object - объект, предназначенный для передачи данных ( в данном случае пароля)
     * @param result - результат валидации пароля с помощью валидатора
     * @param model - модель в контексте MVC
     * @return если есть ошибки - возврат на страницу resetPassword для задачи нового пароля, если токена не существует, возврат на страницу forgotPassword, иначе возврат на страницу login
     */
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute PasswordDTO passwordDTO, BindingResult result,
                                Model model) {
        String tokenString = passwordDTO.getToken(); // Получение токена
        boolean hasErrors = false;
        if(result.hasErrors()) {
            model.addAttribute("token",tokenString);
            model.addAttribute("passwordDTO",passwordDTO);
            Toastr.addErrorsToModel(model,result); // Добавление ошибок в Toastr(JS Плагин для вывода уведомлений на экран)
            hasErrors = true;
        }
        if(passwordDTO.getPassword() == null || !passwordDTO.getPassword().equals(passwordDTO.getPasswordAgain())) { // Проверка паролей на совпадение между друг-другом
            if(!hasErrors) {
                model.addAttribute("token",tokenString);
                model.addAttribute("passwordDTO",passwordDTO);
            }
            Toastr.addErrorToModel(model,"Пароли не совпадают");
            hasErrors = true;
        }
        if(hasErrors)
            return "authorization/resetPassword"; // Если есть ошибки в введённых паролях, тогда возвращем на эту же страницу вводить подходящие пароли
        Token token = resetPasswordTokenService.findByToken(tokenString);

        if(token == null ) { // Если токен не найден
            model.addAttribute("TokenNotFound",true);
            return "authorization/forgotPassword";
        }
        User userFromDB = resetPasswordTokenService.findByToken(tokenString).getUser(); // Получаем юзера
        userFromDB.setEncryptedPassword(passwordDTO.getPassword()); // Устанавливаем новый пароль, шифруя его
        userService.save(userFromDB);
        resetPasswordTokenService.delete(tokenString); // Удаляем токен из БД
        model.addAttribute("success",true); // Параметр для Toastr ( Для отображение уведомления "Успех")
        model.addAttribute("resetPassword",true); // Пароль был удачно сменён.

        return "authorization/login";
    }

    /**
     * Метод, отвечающий за обработку токенов - а именно, публикацию ивентов, создания и добавления новых токенов в БД (если потребуется), проверку токенов на существование, дату истечения и т.д.
     * @param tokenStr - Строковой литерал токена.
     * @param tokenService - Сервис для работы с БД токена.
     * @param model - Модель в контексте MVC.
     * @param tokenType - Тип токена.
     * @param request - HTTP Запрос
     * @return
     */
    private String processTokens(String tokenStr, TokenService tokenService, Model model, TokenType tokenType,HttpServletRequest request) {
        boolean isTokenTypeVerification = tokenType == TokenType.VERIFICATION;
        Token token = tokenService.findByToken(tokenStr);

        if(token == null ) { // Если токен не найден
            model.addAttribute("notFound",true);
            return "index";
        }

        User user = token.getUser(); // Находим пользователя по токену
        LocalDateTime now = LocalDateTime.now(); // Находим локальную дату.
        if ((token.getExpirationDate().isBefore(now))) { // Если срок действия токена уже истёк
            tokenService.delete(tokenStr); // Удаляем токен из БД
            model.addAttribute("expired",true);

            if(isTokenTypeVerification) { // Создаём новый токен для пользователя
                publisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getContextPath()));
            } else {
                publisher.publishEvent(new OnResetPasswordEvent(user,request.getContextPath()));
            }
            return "index"; // Возврат на исходную страничку
        } else if(isTokenTypeVerification) { // Если токен еще не истек, и это токен на подтверждение почты
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Получение ауентификации
            String username = authentication.getName(); // Получение текущего пользователя (юзернейм)
            String usernameFromDataBase = token.getUser().getName(); // Получение юзернейма пользователя, который должен подтвердить почту
            if(!username.equals(usernameFromDataBase)) { // Если юзернеймы не равны, отправляем ошибку НЕвалидный юзер (чтобы другие юзеры не могли подтверждать чужую почту)
                model.addAttribute("nonValidUser",true);
                return "index";
            }
        }
        // FIXME: Придумать, валидацию для ResetPasswordToken по типу VerificationToken (используется проверка на пользователя, подтверждающего почту. В первом случае так поступить не получится т.к. юзер заведомо не залогинен)
        if(isTokenTypeVerification) { // Если тип токена подтверждение почты
            user.setEnabled((byte) 1); // Почта подтверждена, т.к. пошли валидацию сверху, аккаунт "активен"
            userService.saveWithoutPasswordEncryption(user); // Сохраняем новое состоянрие юзера
            tokenService.delete(tokenStr); // Удаляем токен
        } else if(tokenType == TokenType.RESET) { // Если тип токена - смена пароля, перенаправляем на страницу с созданием нового пароля.
            model.addAttribute("token",tokenStr);
            model.addAttribute("passwordDTO",new PasswordDTO());
            return "authorization/resetPassword";
        }

        model.addAttribute("successEmailConfirmation",true);

        return "index";
    }

    /**
     * Метод, отвечающий за обработку заявки на "забыл пароль" посредством POST запроса по адресу "/ajax/process_forgotPassword".
     * @param username - пользователь, забывший пароль (юзернейм либо почта).
     * @param request - HTTP запрос.
     * @return true/false в зависимости от того, нужно ли создавать новый ResetPasswordToken для данного юзера.
     */
    @PostMapping("/ajax/process_forgotPassword")
    public @ResponseBody boolean processForgotPassword(@RequestParam(name = "username",required = false) String username,HttpServletRequest request) {
        User user = null;
            if(username != null) // Если юзернейм или почта задан(а)
                if(username.contains("@"))
                    user = userService.findByEmailIgnoreCase(username); // Если это почта
                else
                    user = userService.findByNameIgnoreCase(username); // Если это юзернейм

        if(user == null )
            return false; // Если пользователь не найден - токен не создаём
        if(user.getResetPasswordToken() == null) // Если нет действующего токена
            publisher.publishEvent(new OnResetPasswordEvent(user,request.getContextPath()));  // Создаём новый токен для данного пользователя
        return true;
    }

    /**
     * Метод, отвечающий за удаление всех пробелов перед и после строк автоматически.
     * @param webDataBinder - байндер для веб-данных
     */
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor =  new StringTrimmerEditor(true); // Считать пустые строки null'ами
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor); // Добавление веб-дата байндера
    }

    /**
     * Метод, отвечающий за обработку логина (Занят ли уже данный логин?) посредством GET запроса
     * @param login - введённый логин
     * @return занят ли данный логин?
     */
    @GetMapping("/ajax/isLoginAlreadyTaken")
    public @ResponseBody boolean isLoginAlreadyTakenAjax(@RequestParam String login) {
        return login == null || registerValidator.isNameAlreadyTaken(login);
    }
    /**
     * Метод, отвечающий за обработку почты (Занята ли уже данная почта?) посредством GET запроса
     * @param email - введённая почта
     * @return занята ли данный почта?
     */
    @GetMapping("/ajax/isEmailAlreadyTaken")
    public @ResponseBody boolean isEmailAlreadyTakenAjax(@RequestParam String email) {
        return email == null || registerValidator.isEmailAlreadyTaken(email);
    }
    /**
     * Метод, отвечающий за обработку паролей (Совпадают ли они?) посредством GET запроса
     * @param password - введённый пароль
     * @param passwordAgain - введённый пароль во второй раз
     * @return НЕ совпадают ли пароли
     */
    @GetMapping("/ajax/doPasswordsNotMatch")
    public @ResponseBody boolean doPasswordsNotMatchAjax(@RequestParam(required = false) String password,@RequestParam(required = false) String passwordAgain) {
        if(password == null || passwordAgain == null)
            return true;
        return !password.equals(passwordAgain);
    }

}
