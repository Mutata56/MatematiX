package mutata.com.github.MatematixProject.controller;



import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер всего, что свзяано с администрированием. Используется для получения доступа к админ-панели, удаления юзера из системы, обновления его данных, пагинации юзеров в CRUD системе.
 * @author Khaliullin Cyrill
 * @version 1.0.0
 *
 * Controller - данный класс является контроллером (предназначен для непосредственной обработки запросов от клиента и возвращения результатов).
 * RequestMapping - обрабытывает все запросы с префиксом /admin/...
 */
@Controller
@RequestMapping("/admin")
public class AdminController {


    /**
     * Класс, созданный для конвертации класса User в JSON формат
     */
    private interface JavaScript {

    }
    private class JavaScriptUser implements JavaScript{
        public final String name;
        public final String email;
        public final byte blocked;
        public final byte enabled;
        public final String role; // Роль пользователя в системе: ADMIN,USER
        JavaScriptUser(User user) {
            this.name = user.getName();
            this.blocked = user.getBlocked();
            this.email = user.getEmail();
            this.enabled = user.getEnabled();
            this.role = user.getRole();
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
     * Класс, созданный для конвертации класса User в JSON формат
     */
    private class JavaScriptToken implements JavaScript {
        public final String token;
        public final String user;
        public final Long id;

        public final String expirationDate;

        JavaScriptToken(VerificationToken token) {
            this.token = token.getToken();
            this.user = token.getUserName();
            this.id = token.getId();
            this.expirationDate = token.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        JavaScriptToken(ResetPasswordToken token) {
            this.token = token.getToken();
            this.user = token.getUserName();
            this.id = token.getId();
            this.expirationDate = token.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

    }

    /**
     *  Объект, предназначенный для работы с БД users (Пользователи)
     */
    private final UserService userService;

    private final ApplicationEventPublisher publisher;

    private final VerificationTokenService verificationTokenService;

    private final ResetPasswordTokenService resetPasswordTokenService;
    @Autowired
    public AdminController(UserService userService,VerificationTokenService verificationTokenService,ResetPasswordTokenService resetPasswordTokenService,ApplicationEventPublisher publisher) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.publisher = publisher;
    }

    /**
     * Метод, отвечающий за показ начальной (Главной) страницы CRDU системы
     * @param token - токен  для защиты уязвимых перед CSRF ресурсов.
     * @param model - модель в контексе MVC.
     * @param sortBy - параметр, по которому необходимо сортировать пользователей в CRUD системе.
     * @param findBy - параметр, по которому необходимо искать пользователей в CRUD системе.
     * @param currentPage - параметр, отвечающий за номер отображаемой страницы в CRUD системе.
     * @param itemsPerPage - параметр, отвечающий за кол-во пользователей, которых нужно показывать в CRUD системе.
     * @param find - параметр, отвечающий за задание паттерна, по которому нужно искать пользователя в CRUD системе.
     * @param sortDirection - параметр, отвечающий за направление сортировки в CRUD системе (Возр., убыв.).
     * @return "/admin/index" - JSP страница, предназначенная для запроса по данному адресу
     */
    @GetMapping(value = {"/index","/",""})
    public String showAdminPanel(CsrfToken token, Model model, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String findBy,
                                 @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) Integer itemsPerPage,
                                 @RequestParam(required = false) String find,
                                 @RequestParam(required = false) String sortDirection) {
        sharedLogicForPagination(token,model,sortBy,findBy,currentPage,itemsPerPage,find,sortDirection,userService);
        return "/admin/index";
    }


    /**
     * Метод, отвечающий за пагинацию сущностей в системе CRUD.
     * @param service - сервис для работы с БД
     * @param model - модель в контексе MVC.
     * @param sortBy - параметр, по которому необходимо сортировать  в CRUD системе.
     * @param findBy - параметр, по которому необходимо искать  в CRUD системе.
     * @param currentPage - параметр, отвечающий за номер отображаемой страницы в CRUD системе.
     * @param itemsPerPage - параметр, отвечающий за кол-во сущностей, которых нужно показывать в CRUD системе.
     * @param find - параметр, отвечающий за задание паттерна, по которому нужно искать пользователя в CRUD системе.
     * @param sortDirection - параметр, отвечающий за направление сортировки в CRUD системе (Возр., убыв.).
     */
    private <T> void pagination(MyService<T> service, Integer itemsPerPage, Integer currentPage, String sortBy, String find, String findBy, String sortDirection, Model model) {

        currentPage = currentPage == null || currentPage <= 0  ? 1 : currentPage; // Если запрос пришёл без указания currentPage
        itemsPerPage = itemsPerPage == null ? 15 : itemsPerPage; // Если запрос пришёл без указания itemsPerPage
        sortDirection = sortDirection == null ? "asc" : sortDirection; // Если запрос пришёл без указания sortDirection
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("itemsPerPage",itemsPerPage);
        model.addAttribute("totalEntities",userService.getCount()); // Сколько всего сущностей в БД
        Page<T> page = null; // Конечный результат, коллекция юзеров
        if(sortBy == null || sortBy.isEmpty()) { // Если не нужно сортировать.
            if(find == null || find.isEmpty()) { // Если не нужно искать по параметру find
                page = service.findAllReturnPage(currentPage - 1,itemsPerPage);
                model.addAttribute("objects",page.getContent()); // Сущности, попавшие в пагинацию
                model.addAttribute("total",page.getTotalPages());
            } else {
                MyResponse<T> response = service.find(currentPage - 1,itemsPerPage,find,findBy);
                model.addAttribute("objects",response.getContent()); // Сущности, попавшие в пагинацию
                model.addAttribute("total",(int) Math.ceil(response.getTotal() / (itemsPerPage * 1.0)));
            }
        } else {
            if(find == null || find.isEmpty()) {
                page = service.findAllSortedBy(currentPage - 1,itemsPerPage,sortBy,sortDirection);
                model.addAttribute("objects",page.getContent()); // Сущности, попавшие в пагинацию
                model.addAttribute("total",page.getTotalPages()); //
            } else {
                MyResponse<User> response =  service.findAndSort(currentPage - 1,itemsPerPage,find,findBy,sortBy,sortDirection);
                model.addAttribute("objects",response.getContent()); // Сущности, попавшие в пагинацию
                model.addAttribute("total",(int) Math.ceil(response.getTotal() / (itemsPerPage * 1.0)));
            }
        }

        model.addAttribute("sortDirection",sortDirection);

    }

    /**
     * Метод обновления сущности посредством ajax запроса по адресу "/ajax/update"

     * @return true/false в зависимости от того, была ли сущность обновлена.
     */
    @PatchMapping("/ajax/updatePerson")
    public @ResponseBody boolean updateAjax(@RequestParam String username,@RequestParam String email, @RequestParam Boolean blocked, @RequestParam Boolean activated,@RequestParam String role ) {
        var tempUser = userService.findByNameIgnoreCase(username.trim());
        boolean hasToBeCreated = tempUser == null; // Отвечает за то, нужно ли создавать нового пользователя ?
        if (hasToBeCreated) {
            tempUser = new User();
            tempUser.setName(username);
            tempUser.setEncryptedPassword("12345"); // Стандартный пароль для нового пользователя
        }
        tempUser.setEmail(email);
        tempUser.setBlocked((byte) (blocked ? 1 : 0));
        tempUser.setEnabled((byte) (activated ? 1 : 0));
        tempUser.setRole(role);
        try {
            if(hasToBeCreated)
                userService.save(tempUser); // Если нужно создавать нового пользоавтеля, то шифруем пароль
            else
                userService.saveWithoutPasswordEncryption(tempUser); // Сохранение пользователя без повторной шифроки пароля ( она была на стадии регистрации)
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    @PatchMapping("/ajax/updateVerificationToken")
    public @ResponseBody String updateVerificationToken(@RequestParam(required = false) String tokenString, @RequestParam String date, @RequestParam String username, @RequestParam(required = false) Long id, HttpServletRequest request) {
        var token = verificationTokenService.findById(id);
        var theUser = userService.findByNameIgnoreCase(username.trim()); // Пользователь, к которому привязан токен
        if(token == null) {
           //  publisher.publishEvent(new OnRegistrationCompleteEvent(theUser,request.getContextPath())); // Создание нового ивента (*Завершение регистрации*), нужно, чтобы подтвердить почту пользователя.
           token = new VerificationToken();
        }
        token.setToken(UUID.randomUUID().toString());
        token.setUser(theUser);

        token.setExpirationDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        verificationTokenService.save(token);
        return token.getToken() + " " + token.getId();
    }

    @PatchMapping("/ajax/updateResetPasswordToken")
    public @ResponseBody String updateResetPasswordToken(@RequestParam(required = false) String tokenString, @RequestParam String date, @RequestParam String username, @RequestParam(required = false) Long id, HttpServletRequest request) {
        var token = resetPasswordTokenService.findById(id);
        var theUser = userService.findByNameIgnoreCase(username.trim()); // Пользователь, к которому привязан токен
        if(token == null) {
           //  publisher.publishEvent(new OnRegistrationCompleteEvent(theUser,request.getContextPath())); // Создание нового ивента (*Завершение регистрации*), нужно, чтобы подтвердить почту пользователя.
           token = new ResetPasswordToken();
        }
        token.setToken(UUID.randomUUID().toString());
        token.setUser(theUser);
        token.setExpirationDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        resetPasswordTokenService.save(token);
        return token.getToken() + " " + token.getId();
    }

    /**
     * Метод удаления сущности по юзернейму/токену посредством ajax запроса по адресу "/ajax/delete"
     * @param data - юзернейм пользователя/ строковое представление токена, которого нужно удалить.
     * @return true/false - взамисимости от того, произошло ли удаление.
     */
    @DeleteMapping("/ajax/delete")
    public @ResponseBody boolean deleteAjax(@RequestParam String data,@RequestParam String clazz) {
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
     * Метод получения юзеров, подходящих по всем параметрам, посредством ajax запроса по адресу "/ajax/process/"
     * @param sortBy - параметр, по которому необходимо сортировать пользователей в CRUD системе.
     * @param findBy - параметр, по которому необходимо искать пользователей в CRUD системе.
     * @param sortDirection - параметр, отвечающий за направление сортировки в CRUD системе (Возр., убыв.).
     * @param itemsPerPage - параметр, отвечающий за кол-во пользователей, которых нужно показывать в CRUD системе.
     * @param currentPage - параметр, отвечающий за номер отображаемой страницы в CRUD системе.
     * @param find - параметр, отвечающий за задание паттерна, по которому нужно искать пользователя в CRUD системе.
     * @param model - модель в контексе MVC.
     * @return list - список юзеров, подходящих под запрос.
     */
    @GetMapping("/ajax/process")
    public @ResponseBody List<JavaScript> processAjax(@RequestParam(required = false) String sortBy, @RequestParam(required = false) String findBy,
                                                    @RequestParam(required = false) String sortDirection,@RequestParam(required = false) Integer itemsPerPage,
                                                    @RequestParam(required = false) Integer currentPage,@RequestParam(required = false) String find,
                                                    Model model, @RequestParam String clazz) {
        sortBy = "noSort".equals(sortBy) ? null : sortBy;
        if("user".equals(clazz)) {
            pagination(userService,itemsPerPage,currentPage,sortBy,find,findBy,sortDirection,model);
            // Извлечение юзеров
            List<User> users = (List<User>) model.getAttribute("objects");
            List<JavaScript> list = new ArrayList<>();
            if(users != null)
                users.forEach(c -> list.add(new JavaScriptUser(c)));
            return list;
        } else if("verificationToken".equals(clazz)){
            pagination(verificationTokenService,itemsPerPage,currentPage,sortBy,find,findBy,sortDirection,model);
            // Извлечение юзеров
            List<VerificationToken> users = (List<VerificationToken>) model.getAttribute("objects");
            List<JavaScript> list = new ArrayList<>();
            if(users != null)
                users.forEach(c -> list.add(new JavaScriptToken(c)));
            return list;
        } else {
            pagination(resetPasswordTokenService,itemsPerPage,currentPage,sortBy,find,findBy,sortDirection,model);
            // Извлечение юзеров
            List<ResetPasswordToken> users = (List<ResetPasswordToken>) model.getAttribute("objects");
            List<JavaScript> list = new ArrayList<>();
            if(users != null)
                users.forEach(c -> list.add(new JavaScriptToken(c)));
            return list;
        }
    }

    /**
     * Общая логика для пагинации. Добавление стандартных параметров в модель.
     */

    private <T> void sharedLogicForPagination(CsrfToken token, Model model, String sortBy, String findBy, Integer currentPage, Integer itemsPerPage, String find, String sortDirection,MyService<T> service) {
        model.addAttribute("sortBy",sortBy);
        model.addAttribute("findBy",findBy);
        model.addAttribute("csrfToken",token.getToken()); // Получение токена из Spring
        sortBy = "noSort".equals(sortBy) ? null : sortBy;
        pagination(service,itemsPerPage,currentPage,sortBy,find,findBy,sortDirection,model);
    }

    /***
     * Показать страницу с верифи. токенами
     */

    @GetMapping("/verificationTokens")
    public String getVerificationTokensPage(CsrfToken token, Model model, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String findBy,
                                @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) Integer itemsPerPage,
                                @RequestParam(required = false) String find,
                                @RequestParam(required = false) String sortDirection) {

        sharedLogicForPagination(token,model,sortBy,findBy,currentPage,itemsPerPage,find,sortDirection,verificationTokenService);
        return "/admin/verificationTokens";
    }

    /***
     * Показать страницу с токенами "забыл пароль"
     */

    @GetMapping("/resetPasswordTokens")
    public String getResetPasswordTokens(CsrfToken token, Model model, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String findBy,
                                @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) Integer itemsPerPage,
                                @RequestParam(required = false) String find,
                                @RequestParam(required = false) String sortDirection) {

        sharedLogicForPagination(token,model,sortBy,findBy,currentPage,itemsPerPage,find,sortDirection,resetPasswordTokenService);
        return "/admin/resetPasswordTokens";
    }
}
