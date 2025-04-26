package mutata.com.github.MatematixProject.controller;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.event.OnResetPasswordEvent;
import mutata.com.github.MatematixProject.service.ResetPasswordTokenService;
import mutata.com.github.MatematixProject.service.UserService;
import mutata.com.github.MatematixProject.service.VerificationTokenService;
import mutata.com.github.MatematixProject.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mutata.com.github.MatematixProject.util.Utils.pagination;
import static mutata.com.github.MatematixProject.util.Utils.sharedLogicForPagination;

/**
 * Контроллер для административных операций:
 * <ul>
 *   <li>Просмотр и управление пользователями</li>
 *   <li>Управление токенами верификации и сброса пароля</li>
 *   <li>CRUD операции через AJAX</li>
 *   <li>Пагинация и сортировка сущностей</li>
 * </ul>
 * <p>Обрабатывает запросы по пути <code>/admin</code>.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * Интерфейс-маркер для объектов, возвращаемых в AJAX (конвертация в JSON).
     */
    private interface JavaScript {
    }

    /**
     * Представление пользователя в формате JSON для JavaScript.
     */
    private class JavaScriptUser implements JavaScript {
        public final String name;
        public final String email;
        public final byte blocked;
        public final byte enabled;
        public final String role;
        public  String avatar;
        public  int rating;
        /**
         * Конструктор формирования JS-объекта из User.
         * @param user исходный пользователь
         */
        JavaScriptUser(User user) {
            this.name = user.getName();
            this.blocked = user.getBlocked();
            this.email = user.getEmail();
            this.enabled = user.getEnabled();
            this.role = user.getRole();
            this.rating = user.getRating();
        }
        /**
         * Конструктор формирования JS-объекта из User и их аватарок.
         * Нужен в случаях, когда требуется загрузка аватарок.
         * @param user исходный пользователь
         * @param avatarInfo информация об аватарке пользователя.
         */
        JavaScriptUser(User user,AvatarInfo avatarInfo) {
            this(user);
            if(avatarInfo != null) this.avatar = Utils.encodeAvatar(avatarInfo.getAvatar());
        }

        @Override
        public String toString() {
            return "JavaScriptUser{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", blocked=" + blocked +
                    ", enabled=" + enabled +
                    ", role='" + role + '\'' +
                    '}';
        }
    }

    /**
     * Представление токена (verification/reset) в формате JSON для JavaScript.
     */
    private class JavaScriptToken implements JavaScript {
        public final String token;
        public final String user;
        public final Long id;
        public final String expirationDate;

        /**
         * Конструктор из VerificationToken.
         * @param token объект VerificationToken
         */
        JavaScriptToken(VerificationToken token) {
            this.token = token.getToken();
            this.user = token.getUserName();
            this.id = token.getId();
            this.expirationDate = token.getExpirationDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        /**
         * Конструктор из ResetPasswordToken.
         * @param token объект ResetPasswordToken
         */
        JavaScriptToken(ResetPasswordToken token) {
            this.token = token.getToken();
            this.user = token.getUserName();
            this.id = token.getId();
            this.expirationDate = token.getExpirationDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    /** Сервис для работы с пользователями. */
    private final UserService userService;
    /** Публикатор событий Spring. */
    private final ApplicationEventPublisher publisher;
    /** Сервис для работы с VerificationToken. */
    private final VerificationTokenService verificationTokenService;
    /** Сервис для работы с ResetPasswordToken. */
    private final ResetPasswordTokenService resetPasswordTokenService;

    /**
     * Конструктор для внедрения зависимостей.
     * @param userService сервис пользователей
     * @param verificationTokenService сервис VerificationToken
     * @param resetPasswordTokenService сервис ResetPasswordToken
     * @param publisher компонент публикации событий
     */
    @Autowired
    public AdminController(
            UserService userService,
            VerificationTokenService verificationTokenService,
            ResetPasswordTokenService resetPasswordTokenService,
            ApplicationEventPublisher publisher) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.publisher = publisher;
    }

    /**
     * Отображение главной страницы админ-панели с пагинацией.
     * @param token CSRF токен для форм
     * @param model модель MVC
     * @param sortBy поле сортировки (опционально)
     * @param findBy поле поиска (опционально)
     * @param currentPage номер текущей страницы (опционально)
     * @param itemsPerPage число элементов на странице (опционально)
     * @param find строка поиска (опционально)
     * @param sortDirection направление сортировки (asc/desc)
     * @return путь к шаблону "/admin/index"
     */
    @GetMapping(value = {"/index","/",""})
    public String showAdminPanel(
            CsrfToken token,
            Model model,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String findBy,
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer itemsPerPage,
            @RequestParam(required = false) String find,
            @RequestParam(required = false) String sortDirection) {
        sharedLogicForPagination(token, model, sortBy, findBy,
                currentPage, itemsPerPage, find, sortDirection, userService);
        return "admin/index";
    }



    /**
     * Обновление/создание пользователя через AJAX.
     * @param username имя пользователя
     * @param email почта
     * @param blocked заблокирован ли пользователь
     * @param activated активирован ли аккаунт
     * @param role роль пользователя
     * @return true если операция успешна
     */
    @PatchMapping("/ajax/updatePerson")
    public @ResponseBody boolean updateAjax(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam Boolean blocked,
            @RequestParam Boolean activated,
            @RequestParam String role) {
        var tempUser = userService.findByNameIgnoreCase(username.trim());
        boolean hasToBeCreated = (tempUser == null);
        if (hasToBeCreated) {
            tempUser = new User();
            tempUser.setName(username);
            tempUser.setEncryptedPassword("12345");
        }
        tempUser.setEmail(email);
        tempUser.setBlocked((byte) (blocked ? 1 : 0));
        tempUser.setEnabled((byte) (activated ? 1 : 0));
        tempUser.setRole(role);
        try {
            if (hasToBeCreated)
                userService.save(tempUser);
            else
                userService.saveWithoutPasswordEncryption(tempUser);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Обновление или создание VerificationToken через AJAX.
     * @param tokenString исходный токен (опционально)
     * @param date строка даты истечения YYYY-MM-DD HH:mm:ss
     * @param username имя пользователя
     * @param id идентификатор токена (опционально)
     * @param request HTTP запрос для контекста
     * @return строка "token id"
     */
    @PatchMapping("/ajax/updateVerificationToken")
    public @ResponseBody String updateVerificationToken(
            @RequestParam(required = false) String tokenString,
            @RequestParam String date,
            @RequestParam String username,
            @RequestParam(required = false) Long id,
            HttpServletRequest request) {
        var token = verificationTokenService.findById(id);
        var theUser = userService.findByNameIgnoreCase(username.trim());
        if (token == null) {
            publisher.publishEvent(new OnRegistrationCompleteEvent(theUser, request.getContextPath()));
            return theUser.getVerificationToken().getToken() + theUser.getVerificationToken().getId();
        }
        token.setToken(UUID.randomUUID().toString());
        token.setUser(theUser);
        token.setExpirationDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        verificationTokenService.save(token);
        return token.getToken() + " " + token.getId();
    }

    /**
     * Обновление или создание ResetPasswordToken через AJAX.
     * @param tokenString исходный токен (опционально)
     * @param date строка даты истечения YYYY-MM-DD HH:mm:ss
     * @param username имя пользователя
     * @param id идентификатор токена (опционально)
     * @param request HTTP запрос для контекста
     * @return строка "token id"
     */
    @PatchMapping("/ajax/updateResetPasswordToken")
    public @ResponseBody String updateResetPasswordToken(
            @RequestParam(required = false) String tokenString,
            @RequestParam String date,
            @RequestParam String username,
            @RequestParam(required = false) Long id,
            HttpServletRequest request) {
        var token = resetPasswordTokenService.findById(id);
        var theUser = userService.findByNameIgnoreCase(username.trim());
        if (token == null) {
            publisher.publishEvent(new OnResetPasswordEvent(theUser, request.getContextPath()));
            return theUser.getResetPasswordToken().getToken() + theUser.getResetPasswordToken().getId();
        }
        token.setToken(UUID.randomUUID().toString());
        token.setUser(theUser);
        token.setExpirationDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        resetPasswordTokenService.save(token);
        return token.getToken() + " " + token.getId();
    }

    /**
     * Удаление пользователя или токена через AJAX.
     * @param data значение username или id токена
     * @param clazz тип сущности: "user", "verificationToken", "resetPasswordToken"
     * @return true при успешном удалении
     */
    @DeleteMapping("/ajax/delete")
    public @ResponseBody boolean deleteAjax(
            @RequestParam String data,
            @RequestParam String clazz) {
        try {
            if ("user".equals(clazz)) {
                userService.delete(userService.findByNameIgnoreCase(data));
            } else if ("verificationToken".equals(clazz)) {
                verificationTokenService.delete(verificationTokenService.findById(Long.parseLong(data)).getToken());
            } else {
                resetPasswordTokenService.delete(resetPasswordTokenService.findById(Long.parseLong(data)).getToken());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Обрабатывает AJAX-запрос для получения списка сущностей с поддержкой пагинации, поиска и сортировки.
     * <p>В зависимости от параметра <code>clazz</code> возвращает либо список пользователей, либо список
     * токенов в контейнере с общим количеством элементов.</p>
     *
     * @param sortBy          поле, по которому требуется сортировка (или <code>null</code> для отключения)
     * @param findBy          поле, по которому осуществляется поиск (или <code>null</code> для отключения)
     * @param sortDirection   направление сортировки: "asc" или "desc" (или <code>null</code>)
     * @param itemsPerPage    количество элементов на странице (или <code>null</code> для значения по умолчанию)
     * @param currentPage     номер запрашиваемой страницы (1‑based, или <code>null</code> для первой)
     * @param find            строка поиска (или <code>null</code> для отключения поиска)
     * @param hasToLoadAvatars флаг, указывающий, нужно ли подгружать данные аватаров для пользователей
     * @param model           модель MVC для передачи атрибутов пагинации и результатов
     * @param clazz           тип запрашиваемых сущностей: "user", "verificationToken" или "resetPasswordToken"
     * @return контейнер {@link Utils.Container}, содержащий список DTO-объектов {@link JavaScript}
     *         и общее число элементов (total pages или total records в зависимости от реализации)
     */
    @GetMapping("/ajax/process")
    public @ResponseBody Utils.Container<List<JavaScript>,Integer> processAjax(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String findBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer itemsPerPage,
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) String find,
            @RequestParam(required = false) boolean hasToLoadAvatars,
            Model model,
            @RequestParam String clazz) {
        sortBy = "noSort".equals(sortBy) ? null : sortBy;
        if ("user".equals(clazz)) {  // Пользователь
            pagination(userService, itemsPerPage, currentPage, sortBy, find, findBy, sortDirection, model);
            List<User> users = (List<User>) model.getAttribute("objects");
            List<JavaScript> list = new ArrayList<>();
            if(users != null) {
                if (hasToLoadAvatars) users.forEach(c -> list.add(new JavaScriptUser(c,c.getAvatarInfo())));
                else  users.forEach(c -> list.add(new JavaScriptUser(c)));
            }

            return new Utils.Container<>(list,(Integer) model.getAttribute("total"));
        } else if ("verificationToken".equals(clazz)) {
            pagination(verificationTokenService, itemsPerPage, currentPage, sortBy, find, findBy, sortDirection, model);
            List<VerificationToken> users = (List<VerificationToken>) model.getAttribute("objects");
            List<JavaScript> list = new ArrayList<>();
            if (users != null) users.forEach(c -> list.add(new JavaScriptToken(c)));
            return new Utils.Container<>(list,(Integer) model.getAttribute("total"));
        } else if ("resetPasswordToken".equals(clazz)) {
            pagination(resetPasswordTokenService, itemsPerPage, currentPage, sortBy, find, findBy, sortDirection, model);
            List<ResetPasswordToken> users = (List<ResetPasswordToken>) model.getAttribute("objects");
            List<JavaScript> list = new ArrayList<>();
            if (users != null) users.forEach(c -> list.add(new JavaScriptToken(c)));
            return new Utils.Container<>(list,(Integer) model.getAttribute("total"));
        } else { // Пользователь ( страница с друзьями, требует отдельной обработки)
            pagination(userService, itemsPerPage, currentPage, sortBy, find, findBy, sortDirection, model);
            List<User> users = (List<User>) model.getAttribute("objects");
            // Собираем карту аватарок друзей
            List<JavaScript> list = new ArrayList<>();
            if (users != null) {
                users.forEach(c -> list.add(new JavaScriptUser(c,c.getAvatarInfo())));
            }
            return new Utils.Container<>(list,(Integer) model.getAttribute("total"));
        }
    }

    /**
     * Отображение страницы с токенами верификации.
     * @param token CSRF токен
     * @param model модель MVC
     * @param sortBy поле сортировки
     * @param findBy поле поиска
     * @param currentPage номер страницы
     * @param itemsPerPage количество элементов
     * @param find строка поиска
     * @param sortDirection направление сортировки
     * @return путь к шаблону "/admin/verificationTokens"
     */
    @GetMapping("/verificationTokens")
    public String getVerificationTokensPage(
            CsrfToken token,
            Model model,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String findBy,
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer itemsPerPage,
            @RequestParam(required = false) String find,
            @RequestParam(required = false) String sortDirection) {
        sharedLogicForPagination(token, model, sortBy, findBy, currentPage, itemsPerPage, find, sortDirection, verificationTokenService);
        return "admin/verificationTokens";
    }

    /**
     * Отображение страницы с токенами сброса пароля.
     * @param token CSRF токен
     * @param model модель MVC
     * @param sortBy поле сортировки
     * @param findBy поле поиска
     * @param currentPage номер страницы
     * @param itemsPerPage количество элементов
     * @param find строка поиска
     * @param sortDirection направление сортировки
     * @return путь к шаблону "/admin/resetPasswordTokens"
     */
    @GetMapping("/resetPasswordTokens")
    public String getResetPasswordTokens(
            CsrfToken token,
            Model model,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String findBy,
            @RequestParam(required = false) Integer currentPage,
            @RequestParam(required = false) Integer itemsPerPage,
            @RequestParam(required = false) String find,
            @RequestParam(required = false) String sortDirection) {
        sharedLogicForPagination(token, model, sortBy, findBy, currentPage, itemsPerPage, find, sortDirection, resetPasswordTokenService);
        return "admin/resetPasswordTokens";
    }
}
