package mutata.com.github.MatematixProject.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import mutata.com.github.MatematixProject.entity.*;
import mutata.com.github.MatematixProject.entity.dto.CommentDTO;
import mutata.com.github.MatematixProject.service.*;
import mutata.com.github.MatematixProject.util.UserUtils;
import mutata.com.github.MatematixProject.util.Utils;
import mutata.com.github.MatematixProject.util.mail.JavaMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static mutata.com.github.MatematixProject.util.Utils.sharedLogicForPagination;

/**
 * Основной контроллер сайта, отвечающий за публичные страницы,
 * форму обратной связи, каталог книг, статьи, закладки,
 * а также AJAX-эндпоинты и real-time обработку комментариев через STOMP.
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Controller
@Slf4j  // Simple Logging Facade for Java
@RequestMapping

public class MainController {

    /**
     * Сервис для управления пользователями.
     */
    private final UserService userService;
    /**
     * Сервис для отправки электронной почты.
     */
    private final JavaMailServiceImpl javaMailSenderImpl;
    /**
     * Сервис для работы с аватарами пользователей.
     */
    private final AvatarInfoService avatarInfoService;
    /**
     * Сервис для управления комментариями.
     */
    private final CommentService commentService;
    /**
     * Шаблон для отправки сообщений через STOMP.
     */
    private final SimpMessagingTemplate messagingTemplate;
    /**
     * Утилиты для работы с пользователем (аватары, статус активности).
     */
    private final UserUtils userUtils;
    /**
     * Сервис для работы с действиями пользователя (лайки/дизлайки).
     */
    private final CommentActionService commentActionService;
    /**
     * Сервис для работы с REST-преобразованием изображений.
     */
    private final MyRestService restService;

    /**
     * Конструктор для внедрения всех необходимых сервисов.
     */
    @Autowired
    public MainController(UserService userService, JavaMailServiceImpl javaMailSenderImpl, AvatarInfoService avatarInfoService, CommentService commentService, UserUtils userUtils, SimpMessagingTemplate template, CommentActionService commentActionService, MyRestService restService) {
        this.userService = userService;
        this.javaMailSenderImpl = javaMailSenderImpl;
        this.avatarInfoService = avatarInfoService;
        this.commentService = commentService;
        this.userUtils = userUtils;
        this.messagingTemplate = template;
        this.commentActionService = commentActionService;
        this.restService = restService;
    }

    /**
     * Отображает главную страницу сайта.
     *
     * @param seenJaxToast значение cookie "jax", указывающее, показывать ли уведомление
     * @param model        модель MVC для передачи атрибутов представлению
     * @return имя JSP-шаблона "index"
     */

    @GetMapping(value = {"/", "/index"})
    public String showIndexPage(@CookieValue(value = "jax", defaultValue = "false") String seenJaxToast, Model model) {
        if ("false".equalsIgnoreCase(seenJaxToast)) { // Видел ли пользователь уведомление "Мы используем JAX"?
            model.addAttribute("seenJaxToast", false);
            model.addAttribute("loadToastr", true);
        } else
            model.addAttribute("seenJaxToast", true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // Получение ауентификации
        if (auth.isAuthenticated()) {
            userUtils.loadAvatar(auth.getName(), model, ""); // Загрузка аватарки
            model.addAttribute("username", auth.getName());
        }
        return "index";
    }

    /**
     * Отображает страницу закладок (список понравившихся статей).
     *
     * @param model модель MVC для передачи списка статей
     * @return имя JSP-шаблона "bookmarks"
     */
    @GetMapping("/bookmarks")
    public String showBookmarksPage(Model model) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Article> articles = userService.findByNameIgnoreCaseAndLoadArticles(currentUser).getArticles(); // Получение всех понравившихся страниц
        model.addAttribute("articles", articles);
        return "bookmarks";
    }

    /**
     * Отображает форму обратной связи "Свяжитесь с нами".
     *
     * @return имя JSP-шаблона "getInTouch"
     */

    @GetMapping("/getInTouch")
    public String showGetInTouchPage() {
        return "getInTouch";
    }

    /**
     * Обрабатывает отправку формы обратной связи.
     *
     * @param name    имя отправителя
     * @param email   email отправителя
     * @param subject тема сообщения
     * @param message текст сообщения
     * @return "success" или строка ошибок для Toastr
     */
    @PostMapping("/process_getInTouch")
    public @ResponseBody String processGetInTouch(@RequestParam(required = false) String name, @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String subject, @RequestParam(required = false) String message, Model model) {
        boolean hasErrors = name == null || subject == null || email == null || message == null || !email.contains("@"); // Базовая валидация
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
        javaMailSenderImpl.send("matematixtest@gmail.com", "MatematiX - Свяжитесь с нами", messageTemplate); // Чтобы не делать обьчную систему логирования просто отсылаем сообщение себе же.
        javaMailSenderImpl.send(email, "MatematiX - Свяжитесь с нами", "Ваше сообщение было успешно доставлено.Спасибо за обратную связь.");
        return "success";
    }

    /**
     * Отображает каталог книг.
     *
     * @return имя JSP-шаблона "books"
     */
    @GetMapping("/books")
    public String showBooksPage() {
        return "books";
    }

    /**
     * Отображает страницу конкретной статьи.
     *
     * @param article имя статьи
     * @param model   модель MVC для передачи данных пользователя
     * @return путь к JSP-шаблону статьи
     */
    @GetMapping("/articles/{article}")
    public String showArticle(@PathVariable String article, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated()) {
            userUtils.loadAvatar(auth.getName(), model, ""); // Загрузить аватарку пользователя для отображения профиля
            model.addAttribute("username", auth.getName());
        }
        return String.format("articles/%s", article);
    }

    /**
     * Отображает информационную страницу о JAX.
     *
     * @return имя JSP-шаблона "jax"
     */
    @GetMapping("/jax")
    public String showJaxPage() {
        return "jax";
    }

    /**
     * Настраивает биндер для удаления пробелов по краям строк из форм.
     *
     * @param webDataBinder биндер веб-данных
     */

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }



    //FIXME Сделать сортировку комментариев;

    /**
     * Инициализирует данные для AJAX-запроса комментариев:
     * форматирует даты, собирает аватарки и статусы.
     *
     * @param receiver    пользователь, чей список комментариев запрашивается
     * @param currentPage номер страницы (1-based)
     * @param count       количество комментариев на страницу
     * @param model       модель MVC для статистики
     * @param toExclude   список пользователей для исключения
     * @return контейнер с комментариями и картой аватарок
     */
    private Utils.Container<List<Comment>, HashMap<String, AvatarInfo>> initializeJSCL(User receiver, int currentPage, int count, Model model, List<String> toExclude) { // Java Script Comment List
        Page<Comment> thePage = commentService.findAllReturnPage(receiver, currentPage - 1, count);
        List<Comment> comments = Utils.reversedView(thePage.getContent());
        HashMap<String, AvatarInfo> usernameToAvatarInfo = new HashMap<>(12);
        HashMap<String, Boolean> isUserAlive = new HashMap<>(12);
        for (Comment comment : comments) {
            String name = comment.getAuthor();
            comment.setStringDate(Utils.formatDate(comment.getDate()));
            if (toExclude == null || !toExclude.contains(comment.getAuthor())) {
                var avatarInfo = avatarInfoService.findByName(name).orElse(null);
                usernameToAvatarInfo.putIfAbsent(name, avatarInfo);
                isUserAlive.putIfAbsent(name, userUtils.isAlive(name));
                if (avatarInfo != null)
                    avatarInfo.setEncodedAvatar(Utils.encodeAvatar(avatarInfo.getAvatar()));
            }
        }
        if (model != null) {
            model.addAttribute("isUserAlive", isUserAlive);
            model.addAttribute("totalPages", thePage.getTotalPages());
        }
        return new Utils.Container<>(comments, usernameToAvatarInfo);
    }

    /**
     * Метод, отвечающий за показ страницы профиля пользователя.
     *
     * @param username - юзернейм пользователя, чей профиль нужно отобразить
     * @param model    - модель в контексте MVC
     * @return главная JSP страница (index)
     */
    @GetMapping(value = {"/profile/{username}", "/profile"})
    public String showProfilePage(@PathVariable(required = false) String username, Model model) {
        if (username == null)
            username = SecurityContextHolder.getContext().getAuthentication().getName(); // Получение пользователя (юз)
        username = StringUtils.capitalize(username.toLowerCase());
        User user = userService.findByNameIgnoreCase(username);
        if (user == null) {
            return "error/404";
        } // Если пользователь не найден возвращаем страницу с ошибкой.
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isMyProfile = currentUserName.equalsIgnoreCase(username);
        if(!isMyProfile)
            model.addAttribute("aFriend", user.getFriends().contains(userService.findByNameIgnoreCase(currentUserName))); // Является ли текущий пользователь другом для данного пользователя

        HashMap<String, AvatarInfo> avatars = new HashMap<>();
        model.addAttribute("name", user.getName());
        model.addAttribute("lvl", "ROLE_ADMIN".equalsIgnoreCase(user.getRole()) ? "Администратор" : "Пользователь");
        model.addAttribute("active", userUtils.isAlive(username));
        userUtils.loadAvatar(currentUserName, avatars, model, "my");
        userUtils.loadAvatar(username, avatars, model, "");
        model.addAttribute("avatars", avatars);
        model.addAttribute("currentUserName", currentUserName);
        model.addAttribute("myProfile", isMyProfile);
        model.addAttribute("rating", user.getRating());
        // Добавляем данные для пагинации комментариев
        int totalPages = (int) Math.ceil(user.getComments().size() / 8.0);
        model.addAttribute("totalPages", totalPages);
        return "profile/index";
    }


    /**
     * Обрабатывает загрузку и обрезку аватарки пользователя.
     * <p>Метод принимает загружаемый файл изображения и параметры обрезки/масштабирования,
     * затем выполняет кропинг, конвертацию в формат WebP и сохранение в БД.</p>
     *
     * @param file   загружаемый файл аватарки (multipart/form-data)
     * @param x      координата X левого верхнего угла области обрезки (до масштабирования)
     * @param y      координата Y левого верхнего угла области обрезки (до масштабирования)
     * @param width  ширина области обрезки (в пикселях итогового изображения)
     * @param height высота области обрезки (в пикселях итогового изображения)
     * @param scaleX коэффициент масштабирования по оси X (для перевода координат и размеров в исходные пиксели)
     * @param scaleY коэффициент масштабирования по оси Y (для перевода координат и размеров в исходные пиксели)
     * @return перенаправление на страницу настроек профиля:
     *         <ul>
     *           <li>с параметром <code>?emptyFile=true</code>, если отсутствуют обязательные параметры</li>
     *           <li>иначе — на URL вида <code>/settings/{username}</code></li>
     */
    @PostMapping(value = "/uploadAvatar", consumes = {"multipart/form-data"})
    public String uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Double x,
            @RequestParam(required = false) Double y,
            @RequestParam(required = false) Double width,
            @RequestParam(required = false) Double height,
            @RequestParam(required = false) Double scaleX,
            @RequestParam(required = false) Double scaleY) {

        // Получаем логин текущего пользователя из контекста Spring Security
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // Базовая валидация: все параметры должны быть заданы и файл не пуст
        if (file.isEmpty() || x == null || y == null || width == null
                || height == null || scaleX == null || scaleY == null) {
            // Если чего-то не хватает — перенаправляем обратно с флагом emptyFile=true
            return "redirect:profile/settings?emptyFile=true";
        }

        try {
            // Читаем изображение из MultipartFile и выполняем обрезку с учётом масштабирования
            BufferedImage original = ImageIO.read(file.getInputStream());
            BufferedImage cropped = Utils.cropAnImage(
                    x, y, scaleX, scaleY, original, width, height);

            // Пытаемся найти уже существующую запись AvatarInfo, иначе создаём новую
            AvatarInfo theInfo = avatarInfoService.findByName(name).orElse(null);
            if (theInfo == null) {
                theInfo = new AvatarInfo();
                theInfo.setUsername(name);
            }

            // Определяем формат исходного файла по расширению
            String format = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf('.') + 1);

            // Конвертируем обрезанное изображение в WebP через внешний REST-сервис
            byte[] croppedBytes = Utils.convertBufferedImageToBytes(cropped, format);
            String base64 = Utils.encodeAvatar(croppedBytes);
            String webpResponse = restService.convertToWebp(format, base64);
            JsonNode node = new ObjectMapper().readTree(webpResponse);

            // Декодируем полученный WebP-контент и сохраняем в БД
            String webpBase64 = node.path("base64String").asText();
            theInfo.setAvatar(Utils.decodeAvatar(webpBase64));
            avatarInfoService.save(theInfo);

        } catch (IOException exception) {
            // Логируем исключение при любых ошибках ввода/вывода
            log.error("Ошибка при обработке аватарки для пользователя {}: {}", name, exception.toString());
        }

        // После успешного сохранения перенаправляем на страницу настроек профиля
        return "redirect:settings/";
    }

    /**
     * Отправляет новый комментарий через STOMP.
     *
     * @param commentDTO DTO с данными комментария
     */
    @MessageMapping("/sendComment") // /application/sendComment
    public void send(CommentDTO commentDTO) {
        commentDTO.setRating(0); // Новому комментарию ставим рейтинг 0
        commentDTO.setStringDate(Utils.formatDate(new Date(commentDTO.getDate()))); // Ставим комментарию актуальную дату
        var temp = Utils.toComment(commentDTO, userService); // Получаем объект Comment
        commentService.save(temp);
        commentDTO.setId(temp.getId());
        messagingTemplate.convertAndSendToUser(commentDTO.getRecipient(), "/queue/messages", commentDTO); // Используем STOMP для отправки комментария
    }

    /**
     * По какой-то причине получаю ошибку когда  JACKSON пытается десериализовать данные из JS(JSON.stringify({"id":id,"username":username,"like": true})) в две строки (public void plusLike(String username,String id,boolean like) {...})
     * Это временное решение
     **/

    /**
     * DTO для передачи лайков/дизлайков комментариев.
     */
    @Data
    static class LikeDTO {
        /**
         * юзернейм пользователя, сделавший какое-то действие с комментарием
         */
        public String username;
        public String id;
        /**
         * Был ли поставлен лайк
         */
        public boolean like;

        public LikeDTO() {

        }
    }

    /**
     * Обрабатывает изменения количества лайков у комментария.
     *
     * @param data данные лайка/дизлайка
     */
    @MessageMapping("/changeLikeAmount") //
    public void changeLikeAmount(LikeDTO data) {
        String username = data.username; // Получаем юзернейм пользователя, сделавший какое-то действие с комментарием
        Long parsedId = Long.parseLong(data.id);
        Comment comment = commentService.findById(parsedId); // Получение комментария по Id
        CommentAction action = commentActionService.findAction(parsedId, username); // Получение сущности "ДЕЙСТВИЕ" с комментарием по ID и username
        if (action == null)
            action = new CommentAction(parsedId, username, (byte) -1); // Если сущности не существует, тогда создать новую
        Byte theAction = action.getAction(); // Байт, обозначающий действие
        boolean isLike = data.isLike();
        if (isLike) // Если пользователь поставил лайк
            comment.setRating(comment.getRating() + (theAction == -1 ? 1 : theAction == 0 ? 2 : -1)); // Рейтинг + 1, если байт действия равен -1 (не был установлен ранее), Рейтинг + 2, если раньше был дизлайк, иначе рейтинг -1 (если раньше стоял лайк)
        else // Если дизлайк
            comment.setRating(comment.getRating() + (theAction == -1 ? -1 : theAction == 1 ? -2 : 1)); // Рейтинг - 1, если байт действия не установлен, Рейтинг + 1, если раньше был дизлайк, Рейтинг -2, если раньше был лайк
        commentService.save(comment);
        action.setAction((byte) (isLike ? 1 : 0)); // Если лайк - байт действия равен 1, если дизлайк, тогда байт действия равен 0, если не задан, то равен -1
        if (isLike) {
            if (theAction == 1) // Если сейчас поставлен лайк, и раньше тоже был лайк, тогда удаляем из БД (ничего по сути не было)
                commentActionService.delete(action);
            else // Если сейчас поставлен лайк, и раньше был поставлен НЕ лайк, тогда сохраняем из БД
                commentActionService.save(action);
        } else {
            if (theAction == 0) // Если раньше был дизлайк, и сейчас тоже дизлайк, удаляем из БД (ничего по сути не было)
                commentActionService.delete(action);
            else // Если сейчас поставлен дизлайк, и раньше был поставлен лайк, тогда сохраняем из БД
                commentActionService.save(action);
        }

        messagingTemplate.convertAndSendToUser(comment.getReceiver().getName(), "/queue/changeLikeAmount", parsedId + " " + comment.getRating());
    }

    /**
     * AJAX: подгружает дополнительные комментарии и аватарки.
     *
     * @param user                       имя пользователя
     * @param currentPage                номер страницы
     * @param usersWhoseAvatarsAreLoaded CSV имен пользователей
     * @return контейнер с комментариями и аватарками
     */
    @GetMapping("/ajax/nextComments")
    public @ResponseBody Utils.Container<List<Comment>, HashMap<String, AvatarInfo>> getNextComments(String user, int currentPage, String usersWhoseAvatarsAreLoaded) {

        currentPage = Math.max(currentPage, 1); // Текущая страница в пагинации
        User receiver = userService.findByNameIgnoreCase(user); // Получатель комментариев
        String[] array = usersWhoseAvatarsAreLoaded.split(","); // Массив пользователей, чьи аватарки загружать не нужно т.к. они были загружены ранее
        var temp = initializeJSCL(receiver, currentPage, 8, null, Arrays.asList(array));

        temp.t = Utils.reversedView(temp.t);
        return temp;
    }

    /**
     * Обрабатывает добавление или удаление друга текущего пользователя.
     *
     * @param name      имя другого пользователя
     * @param isAFriend флаг, является ли он уже другом
     * @return redirect на профиль
     */
    @GetMapping("/ajax/FriendAction")
    public @ResponseBody boolean processFriendAction(@RequestParam String name, @RequestParam boolean isAFriend) {
        if (!isAFriend) {
            userService.addFriend(name, SecurityContextHolder.getContext().getAuthentication().getName());
        } else {
            userService.deleteFriend(name, SecurityContextHolder.getContext().getAuthentication().getName());
        }
        return true;
    }

    /**
     * AJAX: подгружает дополнительных друзей и их аватарки.
     *
     * @param user                       имя пользователя
     * @param currentPage                номер страницы
     * @param usersWhoseAvatarsAreLoaded CSV имен пользователей
     * @return список AvatarInfo для новых друзей
     */
    @GetMapping("/ajax/nextFriends")
    public @ResponseBody List<AvatarInfo> getNextFriends(String user, int currentPage, String usersWhoseAvatarsAreLoaded, int totalUsers) {
        currentPage = Math.max(currentPage, 1);
        if((currentPage) * 6 >= totalUsers) return null;
        List<String> array = Arrays.asList(usersWhoseAvatarsAreLoaded.split(","));
        var friends = userService.findAllFriendsReturnPage(userService.findByNameIgnoreCase(user), currentPage, 6).getContent(); // Пагинация
        List<AvatarInfo> infos = new ArrayList<>(); // Аватарки друзей, которые нужно загрузить будут храниться тут
        for (User tempUser : friends) { // Для каждого друга, если его username не содежрится в списке на исключение (ава уже загружена) загружаме аватарку.
            if (!array.contains(tempUser.getName()))
                infos.add(avatarInfoService.findByNameOrReturnEmpty(tempUser.getName()));
        }

        return infos;
    }

    /**
     * AJAX: проверяет, онлайн ли указанный пользователь.
     *
     * @param username имя пользователя
     * @return true, если пользователь активен
     */
    @GetMapping("/ajax/isUserAlive")
    public @ResponseBody Boolean isUserAlive(String username) {
        return userUtils.isAlive(username);
    }

    /**
     * Отображает страницу настроек профиля.
     *
     * @param model модель MVC для передачи атрибутов
     * @return имя JSP-шаблона "profile/settings" или "error/404"
     */
    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // Получение пользователя (юз)
        username = StringUtils.capitalize(username.toLowerCase());
        User user = userService.findByNameIgnoreCase(username);
        if (user == null) {
            return "error/404";
        } // Если пользователь не найден возвращаем страницу с ошибкой.
        HashMap<String, AvatarInfo> avatars = new HashMap<>();
        model.addAttribute("name", username);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("active", userUtils.isAlive(username));
        userUtils.loadAvatar(username, avatars, model, "my");
        model.addAttribute("avatars", avatars);
        model.addAttribute("hasToActivateEmail", user.getEnabled() == 1 ? 0 : 1);
        return "profile/settings";
    }

    /**
     * Удаляет аккаунт текущего пользователя и завершает сессию.
     *
     * @param request HTTP-запрос для выполнения logout и управления сессией
     * @return redirect на страницу логина
     * @throws ServletException при ошибке logout
     */
    @GetMapping("/deleteAccount")
    public String deleteAccount(HttpServletRequest request) throws ServletException {
        userService.delete(userService.findByNameIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName()));
        request.logout();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/login";
    }

    /**
     * AJAX-эндпоинт для изменения электронного адреса текущего пользователя.
     * <p>Получает новый email из параметра запроса, обновляет поле в сущности пользователя
     * и сохраняет изменения без повторного шифрования пароля.</p>
     *
     * @param request текущий HTTP-запрос (используется для контекста безопасности, не модифицируется)
     * @param email   новый адрес электронной почты, переданный в запросе
     * @return <code>true</code>, если обновление прошло успешно; <code>false</code> при возникновении исключения
     */
    @GetMapping("/ajax/changeEmail")
    public @ResponseBody boolean changeEmail(HttpServletRequest request,
                                             @RequestParam String email) throws ServletException {
        try {
            var user = userService.findByNameIgnoreCase(
                    SecurityContextHolder.getContext().getAuthentication().getName());
            user.setEmail(email);
            user.setEnabled((byte)0);
            userService.saveWithoutPasswordEncryption(user);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Отображает страницу списка друзей для указанного пользователя.
     * <p>Если путь запроса содержит параметр <code>username</code>, будет показан профиль этого пользователя,
     * иначе — текущего аутентифицированного пользователя.</p>
     * <ol>
     *   <li>Получает имя пользователя из пути или из контекста Spring Security.</li>
     *   <li>Капитализирует имя для единообразия поиска.</li>
     *   <li>Пытается загрузить объект {@link User} по имени; при отсутствии — возвращает страницу ошибки 404.</li>
     *   <li>Делает пагинацию друзей (6 на страницу), добавляет в модель общее количество друзей.</li>
     *   <li>Собирает аватарки друзей в карту для отображения.</li>
     *   <li>Добавляет в модель данные о количестве страниц комментариев и карту аватарок.</li>
     * </ol>
     *
     * @param username имя пользователя из URL (необязательный путьовой параметр)
     * @param model    MVC-модель для передачи атрибутов во view
     * @return имя JSP/Thymeleaf-шаблона для рендеринга страницы друзей или "error/404" при отсутствии пользователя
     */
    @GetMapping(value = {"/friends/{username}", "/friends"})
    public String showFriendsPage(@PathVariable(required = false) String username,
                                  CsrfToken token,
                                  Model model,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String findBy,
                                  @RequestParam(required = false) Integer currentPage,
                                  @RequestParam(required = false) Integer itemsPerPage,
                                  @RequestParam(required = false) String find,
                                  @RequestParam(required = false) String sortDirection) {


        // Если username не передан, используем текущего аутентифицированного пользователя
        if (username == null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        // Приводим имя к единому формату (первая буква заглавная)
        username = StringUtils.capitalize(username.toLowerCase());

        // Загружаем пользователя из БД
        User user = userService.findByNameIgnoreCase(username);
        if (user == null) {
            // Если пользователь не найден, возвращаем страницу 404
            return "error/404";
        }

        if(findBy == null) {
            findBy = "friends";
        }
        if("friends".equals(findBy)) find = username;


        sharedLogicForPagination(token, model, sortBy, findBy, currentPage, itemsPerPage, find, sortDirection, userService);
        // Пагинация друзей: первая страница, по 6 элементов на страницу
        Long countOfFriends = (Long) model.getAttribute("totalEntities");
        var friends = (List<User>) model.getAttribute("objects");

        model.addAttribute("name", username);

        // Собираем карту аватарок друзей
        HashMap<String, AvatarInfo> avatars = new HashMap<>();
        if (countOfFriends != 0) {
            friends.forEach(theUser -> theUser.storeAvatar(avatars));
        }



        // Карта аватарок для отображения в шаблоне
        model.addAttribute("avatars", avatars);

        return "profile/friends";  // Здесь должно быть имя шаблона, напр. "friends"
    }
    /**
     * Отображает страницу поиска пользователей с поддержкой пагинации и сортировки.
     * <p>Вызывает общий метод sharedLogicForPagination для заполнения модели данными,
     * затем собирает карту аватаров пользователей и добавляет её в модель для отображения.</p>
     *
     * @param token         CSRF-токен для защиты запросов
     * @param model         модель MVC для передачи атрибутов пагинации, списка пользователей и аватаров
     * @param sortBy        поле, по которому выполняется сортировка (или <code>null</code> для отключения)
     * @param findBy        поле, по которому осуществляется поиск (или <code>null</code>)
     * @param currentPage   номер текущей страницы (1-based, или <code>null</code> для первой)
     * @param itemsPerPage  количество элементов на странице (или <code>null</code> для значения по умолчанию)
     * @param find          строка поиска (или <code>null</code> для отключения)
     * @param sortDirection направление сортировки: "asc" или "desc" (или <code>null</code>)
     * @return имя шаблона Thymeleaf "profile/find" для отображения страницы поиска
     */
    @GetMapping("/find")
    public String showFindPage(
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
        Long countOfEntities = (Long) model.getAttribute("totalEntities");
        var users = (List<User>) model.getAttribute("objects");

        // Собираем карту аватарок друзей
        HashMap<String, AvatarInfo> avatars = new HashMap<>();
        if (countOfEntities != 0) {
            users.forEach(theUser -> theUser.storeAvatar(avatars));
        }



        // Карта аватарок для отображения в шаблоне
        model.addAttribute("avatars", avatars);

        return "profile/find";
    }
}
