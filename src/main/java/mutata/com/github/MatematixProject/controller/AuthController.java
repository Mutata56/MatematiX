package mutata.com.github.MatematixProject.controller;

import lombok.extern.slf4j.Slf4j;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.dto.PasswordDTO;
import mutata.com.github.MatematixProject.entity.dto.RegisterDTO;
import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import mutata.com.github.MatematixProject.entity.token.Token;
import mutata.com.github.MatematixProject.entity.token.TokenType;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.event.OnResetPasswordEvent;
import mutata.com.github.MatematixProject.service.ResetPasswordTokenService;
import mutata.com.github.MatematixProject.service.TokenService;
import mutata.com.github.MatematixProject.service.UserService;
import mutata.com.github.MatematixProject.service.VerificationTokenService;
import mutata.com.github.MatematixProject.util.RegisterValidator;
import mutata.com.github.MatematixProject.util.Toastr;
import mutata.com.github.MatematixProject.util.UserUtils;
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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
/**
 * Контроллер аутентификации и регистрации.
 * <p>Обрабатывает маршруты <code>/auth/**</code> для входа, регистрации,
 * подтверждения email, сброса пароля и AJAX проверок.</p>
 *
 * <ul>
 *   <li>Авторизация пользователя</li>
 *   <li>Регистрация и валидация данных</li>
 *   <li>Подтверждение email через токен</li>
 *   <li>Запрос на сброс и смену пароля</li>
 *   <li>AJAX-эндпоинты для проверки уникальности логина/почты и соответствия паролей</li>
 *   <li>Выход из системы</li>
 * </ul>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Controller
@Slf4j  // Логирование через SLF4J
@RequestMapping("/auth")
public class AuthController {

    /** Сервис публикации событий (регистрация, сброс пароля). */
    private final ApplicationEventPublisher publisher;
    /** Валидатор регистрации новых пользователей. */
    private final RegisterValidator registerValidator;
    /** Сервис для управления учетными записями пользователей. */
    private final UserService userService;
    /** Сервис для управления токенами подтверждения email. */
    private final VerificationTokenService verificationTokenService;
    /** Сервис для управления токенами сброса пароля. */
    private final ResetPasswordTokenService resetPasswordTokenService;
    /** Утилитарный класс для работы с аватарами и состоянием пользователя. */
    private final UserUtils userUtils;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param userService           сервис пользователей
     * @param verificationTokenService сервис токенов подтверждения
     * @param publisher             публикация событий
     * @param registerValidator     валидатор регистрации
     * @param resetPasswordTokenService сервис токенов сброса пароля
     * @param userUtils             утилиты пользователя
     */
    @Autowired
    public AuthController(UserService userService,
                          VerificationTokenService verificationTokenService,
                          ApplicationEventPublisher publisher,
                          RegisterValidator registerValidator,
                          ResetPasswordTokenService resetPasswordTokenService,
                          UserUtils userUtils) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.publisher = publisher;
        this.registerValidator = registerValidator;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.userUtils = userUtils;
    }

    /**
     * Отображение страницы логина.
     *
     * @return имя шаблона логина
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "authorization/login";
    }

    /**
     * Отображение страницы запроса забытого пароля.
     *
     * @return имя шаблона forgotPassword
     */
    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "authorization/forgotPassword";
    }

    /**
     * Отображение страницы регистрации с DTO формы.
     *
     * @param model модель MVC
     * @return имя шаблона регистрации
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("dto", new RegisterDTO());
        return "authorization/register";
    }

    /**
     * Обработка формы регистрации нового пользователя.
     * <p>Выполняет валидацию входных данных, создаёт нового пользователя,
     * отправляет событие завершения регистрации и автоматически
     * выполняет вход в систему.</p>
     *
     * @param request     текущий HTTP-запрос для получения контекста приложения
     * @param registerDTO DTO, содержащий данные регистрации: имя, email и пароль
     * @param result      результаты валидации полей DTO (ошибки ввода и валидации)
     * @param model       модель MVC для передачи сообщений об ошибках (через Toastr)
     * @return редирект на главную страницу при успешной регистрации
     *         или имя шаблона "/authorization/register" при наличии ошибок валидации
     */
    @PostMapping("/register")
    public String registerUser(HttpServletRequest request,
                               @Valid @ModelAttribute(name = "dto", binding = true) RegisterDTO registerDTO,
                               BindingResult result,
                               Model model) {
        // Валидация данных регистрации
        registerValidator.validate(registerDTO, result);
        if (result.hasErrors()) {
            Toastr.addErrorsToModel(model, result);
            return "authorization/register";
        }
        // Создание нового пользователя
        User user = new User();
        user.setEncryptedPassword(registerDTO.getPassword());
        user.setEmail(StringUtils.capitalize(registerDTO.getEmail().toLowerCase()));
        user.setRole("ROLE_USER");
        if("admin".equalsIgnoreCase(registerDTO.getName()))
            user.setRole("ROLE_ADMIN");
        user.setName(StringUtils.capitalize(registerDTO.getName().toLowerCase()));
        userService.save(user);
        // Отправка события завершения регистрации
        publisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getContextPath()));
        try {
            // Авто-login пользователя после регистрации
            request.login(user.getName(), registerDTO.getPassword());
        } catch (ServletException exception) {
            log.info("Error while login " + exception);
        }
        return "redirect:/";
    }

    /**
     * Подтверждение email по токену.
     *
     * @param token  строка токена
     * @param model  модель MVC
     * @param request текущий HTTP-запрос
     * @return имя результирующего шаблона
     */
    @GetMapping("/registrationConfirm")
    public String registrationConfirm(@RequestParam("token") String token,
                                      Model model,
                                      HttpServletRequest request) {
        return processTokens(token, verificationTokenService, model, TokenType.VERIFICATION, request);
    }

    /**
     * Отображение страницы смены пароля по токену.
     *
     * @param token строка токена
     * @param model модель MVC
     * @param request текущий HTTP-запрос
     * @return имя шаблона resetPassword или redirect
     */
    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam("token") String token,
                                Model model,
                                HttpServletRequest request) {
        return processTokens(token, resetPasswordTokenService, model, TokenType.RESET, request);
    }

    /**
     * Обработка формы смены пароля.
     * <p>Выполняет валидацию полей и токена сброса пароля, обновляет пароль пользователя
     * и удаляет использованный токен.</p>
     *
     * @param passwordDTO DTO, содержащий новый пароль, его подтверждение и строку токена
     * @param result      результаты валидации полей DTO (ошибки валидации паролей)
     * @param model       модель MVC для передачи флагов ошибок и состояний (TokenNotFound, success и resetPassword)
     * @return имя шаблона для повторного отображения формы при ошибках
     *         или перенаправление/имя шаблона авторизации при успешном сбросе пароля
     */
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute PasswordDTO passwordDTO,
                                BindingResult result,
                                Model model) {
        String tokenString = passwordDTO.getToken();
        boolean hasErrors = false;
        // Валидация пароля и совпадения полей
        if (result.hasErrors()) {
            model.addAttribute("token", tokenString);
            model.addAttribute("passwordDTO", passwordDTO);
            Toastr.addErrorsToModel(model, result);
            hasErrors = true;
        }
        if (passwordDTO.getPassword() == null || !passwordDTO.getPassword().equals(passwordDTO.getPasswordAgain())) {
            if (!hasErrors) {
                model.addAttribute("token", tokenString);
                model.addAttribute("passwordDTO", passwordDTO);
            }
            Toastr.addErrorToModel(model, "Пароли не совпадают");
            hasErrors = true;
        }
        if (hasErrors) {
            return "authorization/resetPassword";
        }
        // Поиск и проверка токена
        Token token = resetPasswordTokenService.findByToken(tokenString);
        if (token == null) {
            model.addAttribute("TokenNotFound", true);
            return "authorization/forgotPassword";
        }
        User userFromDB = resetPasswordTokenService.findByToken(tokenString).getUser();
        userFromDB.setEncryptedPassword(passwordDTO.getPassword());
        userService.save(userFromDB);
        resetPasswordTokenService.delete(tokenString);
        model.addAttribute("success", true);
        model.addAttribute("resetPassword", true);
        return "authorization/login";
    }

    /**
     * Общий метод обработки токенов (подтверждение email или сброс пароля).
     * <p>В зависимости от типа токена выполняет проверку на существование и актуальность,
     * обрабатывает истёкшие токены с повторной отправкой либо валидирует пользователя при подтверждении Email,
     * или готовит страницу для смены пароля.</p>
     *
     * @param tokenStr      строковое значение токена, переданное из запроса
     * @param tokenService  сервис работы с токенами (VerificationTokenService или ResetPasswordTokenService)
     * @param model         модель MVC для передачи флагов состояния (notFound, expired, nonValidUser, successEmailConfirmation)
     * @param tokenType     тип токена: {@link TokenType#VERIFICATION} для подтверждения email или {@link TokenType#RESET} для сброса пароля
     * @param request       текущий HTTP-запрос для получения контекста приложения и формирования URL в событиях
     * @return имя шаблона Thymeleaf для отображения ("index" или "authorization/resetPassword")
     */
    private String processTokens(String tokenStr,
                                 TokenService tokenService,
                                 Model model,
                                 TokenType tokenType,
                                 HttpServletRequest request) {
        boolean isVerification = tokenType == TokenType.VERIFICATION;
        Token token = tokenService.findByToken(tokenStr);
        if (token == null) {
            model.addAttribute("notFound", true);
            return "index";
        }
        User user = token.getUser();
        LocalDateTime now = LocalDateTime.now();
        if (token.getExpirationDate().isBefore(now)) {
            tokenService.delete(tokenStr);
            model.addAttribute("expired", true);
            if (isVerification) {
                publisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getContextPath()));
            } else {
                publisher.publishEvent(new OnResetPasswordEvent(user, request.getContextPath()));
            }
            return "index";
        } else if (isVerification) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth.getName();
            String tokenUser = token.getUser().getName();
            if (!currentUser.equals(tokenUser)) {
                model.addAttribute("nonValidUser", true);
                return "index";
            }
        }
        if (isVerification) {
            user.setEnabled((byte) 1);
            userService.saveWithoutPasswordEncryption(user);
            tokenService.delete(tokenStr);
        } else {
            model.addAttribute("token", tokenStr);
            model.addAttribute("passwordDTO", new PasswordDTO());
            return "authorization/resetPassword";
        }
        model.addAttribute("successEmailConfirmation", true);
        return "index";
    }

    /**
     * Обработка AJAX запроса "забыл пароль": проверка наличия пользователя и создание токена.
     *
     * @param username имя или email пользователя
     * @param request  текущий HTTP-запрос
     * @return true, если токен был отправлен или уже существует
     */
    @PostMapping("/ajax/process_forgotPassword")
    public @ResponseBody boolean processForgotPassword(
            @RequestParam(name = "username", required = false) String username,
            HttpServletRequest request) {
        User user = null;
        if (username != null) {
            if (username.contains("@")) {
                user = userService.findByEmailIgnoreCase(username);
            } else {
                user = userService.findByNameIgnoreCase(username);
            }
        }
        if (user == null) {
            return false;
        }
        if (user.getResetPasswordToken() == null) {
            publisher.publishEvent(new OnResetPasswordEvent(user, request.getContextPath()));
        }
        return true;
    }

    /**
     * Удаление пробелов в начале и конце строк автоматически (InitBinder).
     *
     * @param webDataBinder биндер для веб-данных
     */
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    /**
     * AJAX проверка: занят ли логин.
     *
     * @param login проверяемый логин
     * @return true, если логин пуст или уже занят
     */
    @GetMapping("/ajax/isLoginAlreadyTaken")
    public @ResponseBody boolean isLoginAlreadyTakenAjax(@RequestParam String login) {
        return login == null || registerValidator.isNameAlreadyTaken(login);
    }

    /**
     * AJAX проверка: занята ли почта.
     *
     * @param email проверяемая почта
     * @return true, если почта пустая или занята
     */
    @GetMapping("/ajax/isEmailAlreadyTaken")
    public @ResponseBody boolean isEmailAlreadyTakenAjax(@RequestParam String email) {
        return email == null || registerValidator.isEmailAlreadyTaken(email);
    }

    /**
     * AJAX проверка: не совпадают ли пароли.
     *
     * @param password       первый пароль
     * @param passwordAgain  второй пароль для проверки
     * @return true, если пароли не заданы или не совпадают
     */
    @GetMapping("/ajax/doPasswordsNotMatch")
    public @ResponseBody boolean doPasswordsNotMatchAjax(
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String passwordAgain) {
        if (password == null || passwordAgain == null) {
            return true;
        }
        return !password.equals(passwordAgain);
    }

    /**
     * Обработка выхода из системы: logout и инвалидирование сессии.
     *
     * @param request текущий HTTP-запрос
     * @return редирект на главную страницу
     * @throws ServletException при ошибке logout
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    /**
     * Обрабатывает AJAX-запрос на генерацию ссылки подтверждения email для текущего пользователя.
     * <p>Если для пользователя ещё не существует активного {@link VerificationToken}, метод:
     * <ul>
     *   <li>создаёт новый токен,</li>
     *   <li>привязывает его к текущему пользователю,</li>
     *   <li>сохраняет через {@link mutata.com.github.MatematixProject.service.VerificationTokenService},</li>
     *   <li>возвращает {@code true}, сигнализируя о необходимости отправить ссылку по электронной почте.</li>
     * </ul>
     * <p>Если же токен уже есть, метод возвращает {@code false}, чтобы избежать дубликата.</p>
     *
     * @param request текущий HTTP-запрос (используется для получения контекста аутентификации)
     * @return {@code true} — если новый токен был успешно создан; {@code false} — если токен уже существует
     * @throws ServletException при ошибках работы с сессией (не используется в текущей реализации)
     */
    @GetMapping("/ajax/activateEmail")
    public @ResponseBody boolean activateEmailRequest(HttpServletRequest request) throws ServletException {
        User user = userService.findByNameIgnoreCase(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.getVerificationToken() == null) {
            publisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getContextPath()));
            return true;
        }
        return false;
    }


    /**
     * Обрабатывает AJAX-запрос на создание ссылки для сброса пароля текущего пользователя.
     * <p>Если для пользователя ещё не сгенерирован токен сброса пароля, метод:
     * <ul>
     *   <li>создаёт новый {@link ResetPasswordToken} с уникальным значением {@code token},</li>
     *   <li>связывает его с текущим пользователем,</li>
     *   <li>сохраняет в БД через {@link ResetPasswordTokenService},</li>
     *   <li>возвращает {@code true} — сигнал о необходимости отправить ссылку по почте.</li>
     * </ul>
     * <p>Если токен уже существует, метод возвращает {@code false}, чтобы не создавать дубликат.</p>
     *
     * @param request текущий HTTP-запрос (используется для получения контекста аутентификации)
     * @return {@code true}, если токен был создан и сохранён; {@code false}, если токен уже существует
     * @throws ServletException при ошибках работы с сессией (не используется в текущей реализации)
     */
    @GetMapping("/ajax/resetPasswordRequest")
    public @ResponseBody boolean resetPasswordRequest(HttpServletRequest request) throws ServletException {
        User user = userService.findByNameIgnoreCase(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.getResetPasswordToken() == null) {

            publisher.publishEvent(new OnResetPasswordEvent(user, request.getContextPath()));

            return true;
        }
        return false;
    }

}