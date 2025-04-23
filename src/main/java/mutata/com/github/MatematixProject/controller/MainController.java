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
/**
 * Основной контроллер сайта, связан с нач. страницой, настройками, статяьми, закладками, страницей свяжитесь с нами и т.д. ( всё что не касается администрирования, ошибок, защиты)
 * @author Khaliullin Cyrill
 * @version 1.0.0
 *
 * Controller - данный класс является контроллером (предназначен для непосредственной обработки запросов от клиента и возвращения результатов).
 * RequestMapping - обрабытывает все запросы
 * Slf4j - Предоставляет интерфейс для логирования, направляя вызовы в конкретную реализацию логгера.
 */
@Controller
@Slf4j  // Simple Logging Facade for Java
@RequestMapping

public class MainController {
    /**
     * Объект для работы с БД users
     */
    private final UserService userService;
    /**
     * Объект для работы с почтой
     */

    private final JavaMailServiceImpl javaMailSenderImpl;
    /**
     * Объект для работы с аватарками пользователей
     */

    private final AvatarInfoService avatarInfoService;
    /**
     * Объект для работы с комментариями
     */

    private final CommentService commentService;
    /**
     * Объект для работы с протоколом STOMP (Simple Text Oriented Messaging Protocol)
     */

    private final SimpMessagingTemplate messagingTemplate;
    /**
     * Объект с утилками для работы с юзером
     */

    private final UserUtils userUtils;
    /**
     * Объект для работы с рейтингом комментариев (БД commentAction)
     */

    private final CommentActionService commentActionService;
    /**
     * Объект для работы с REST запросами
     */

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

    /**
     * Метод, отвечающий за показ начальной страницы.
     * @param model - модель в контексте MVC
     * @return index - JSP Страница
     */

    @GetMapping(value = {"/","/index"})
    public String showIndexPage(@CookieValue(value = "jax",defaultValue = "false") String seenJaxToast,Model model) {
        if("false".equalsIgnoreCase(seenJaxToast)) { // Видел ли пользователь уведомление "Мы используем JAX"?
            model.addAttribute("seenJaxToast", false);
            model.addAttribute("loadToastr",true);
        }
        else
            model.addAttribute("seenJaxToast", true);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // Получение ауентификации
        if(auth.isAuthenticated()) {
            userUtils.loadAvatar(auth.getName(),model,""); // Загрузка аватарки
            model.addAttribute("username",auth.getName());
        }
        return "index";
    }

    /**
     * Метод, отвечающий за показ закладок (понравившихся статей).
     * @param model - модель в контексте MVC
     * @return bookmarks - JSP Страница
     */
    @GetMapping("/bookmarks")
    public String showBookmarksPage(Model model) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Article> articles = userService.findByNameIgnoreCaseAndLoadArticles(currentUser).getArticles(); // Получение всех понравившихся страниц
        model.addAttribute("articles",articles);
        return "bookmarks";
    }

    /**
     * Метод, отвечающий за показ страницы "свяжитесь с нами"
     * @return getInTouch - JSP Страница
     */

    @GetMapping("/getInTouch")
    public String showGetInTouchPage() {
        return "getInTouch";
    }

    /**
     * Метод, отвечающий за обработку данных на странице "свяжитесь с нами" посредством POST запроса
     * @return success - указание, для Toastr, что операция прошла успешно
     */

    @PostMapping("/process_getInTouch")
    public @ResponseBody String processGetInTouch(@RequestParam(required = false) String name,@RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String subject,@RequestParam(required = false) String message,Model model) {
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
     * Метод, отвечающий за показ страницы "книги"
     * @return books - JSP страница с каталогом книг
     */
    @GetMapping("/books")
    public String showBooksPage() {
        return "books";
    }

    /**
     * Метод, отвечающий за показ страницы статьи
     * @param article - наименование статьи
     * @param model - модель в контексте MVC
     * @return articles/*article* - JSP страница статьи
     */
    @GetMapping("/articles/{article}")
    public String showArticle(@PathVariable String article,Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.isAuthenticated()) {
            userUtils.loadAvatar(auth.getName(),model,""); // Загрузить аватарку пользователя для отображения профиля
            model.addAttribute("username",auth.getName());
        }
        return String.format("articles/%s",article);
    }
    /**
     * Метод, отвечающий за показ страницы "Мы используем Jax"
     * @return jax - JSP страница статьи
     */
    @GetMapping("/jax")
    public String showJaxPage() {
        return "jax";
    }

    /**
     * Метод, отвечающий за удаление всех пробелов перед и после строк автоматически.
     * @param webDataBinder - байндер для веб-данных
     */

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor =  new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    /**
     * Простой класс контейнер
     * @param <T> - первый параметр
     * @param <R> - второй параметр
     */
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

    /**
     * Метод, отвечающий за показ страницы профиля пользователя.
     * @param username - юзернейм пользователя, чей профиль нужно отобразить
     * @param model - модель в контексте MVC
     * @return главная JSP страница (index)
     */
    @GetMapping(value = {"/profile/{username}","/profile"})
    public String showProfilePage(@PathVariable(required = false) String username,Model model) {
        if(username == null)
            username = SecurityContextHolder.getContext().getAuthentication().getName(); // Получение пользователя (юз)
        username = StringUtils.capitalize(username.toLowerCase());
        User user = userService.findByNameIgnoreCase(username);
        if (user == null) {
            return "error/404";
        } // Если пользователь не найден возвращаем страницу с ошибкой.
        MyResponse<User> friends = userService.findAllFriendsReturnPage(user,0,6); // Найти всех друзей из БД. Пагинация
        long countOfFriends = friends.getTotal(); // Кол-во друзей всего
        model.addAttribute("countOfFriends",countOfFriends);
        HashMap<String,AvatarInfo> avatars = new HashMap<>();
        model.addAttribute("aFriend",false); // Является ли текущий пользователь другом для данного пользователя

        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(countOfFriends != 0) {
            friends.getContent().forEach(theUser -> theUser.storeAvatar(avatars)); // Для каждого друга получить аватарки друзей
            var friendNames = friends.getContent().stream().map(User::getName).toList(); // Получить имена друзей
            model.addAttribute("friends",friendNames);
            if(friendNames.contains(currentUserName))
                model.addAttribute("aFriend",true); // Если в друзьях содержится текущее имя
        }

        model.addAttribute("rating",user.getRating());

        model.addAttribute("totalPages",(int) Math.ceil(user.getComments().size() / (8 * 1.0))); // Пагинация
        model.addAttribute("name",user.getName());
        model.addAttribute("lvl","ROLE_ADMIN".equalsIgnoreCase(user.getRole()) ? "Администратор" : "Пользователь");
        model.addAttribute("active",userUtils.isAlive(username));
        userUtils.loadAvatar(currentUserName,avatars,model,"my");
        userUtils.loadAvatar(username,avatars,model,"");
        model.addAttribute("currentUserName",currentUserName);
        model.addAttribute("avatars",avatars);
        model.addAttribute("myProfile",currentUserName.equalsIgnoreCase(username));
        return "profile/index";
    }



    /**
     * Метод, отвечающий за загрузку новой аватарки, базовую валидацию.
     * @param file - файл, который стоит загрузить
     * @param x - соотв. координата при обрезке картинки (начало)
     * @param y - соотв. координата при обрезке картинки (начало)
     * @param width - ширина картинки (для вычисления конечной координаты широты: x + width)
     * @param height - высота картинки (для вычисления конечной координаты высоты: y + height
     * @param scaleX - коэф. увеличения картинки по Х
     * @param scaleY - коэф. увеличения картинки по У
     * @return profile/*name* - JSP страница профиля
     */
    @PostMapping(value = "/uploadAvatar",consumes = {"multipart/form-data"})
    public String uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Double x, @RequestParam(required = false) Double y,
                               @RequestParam(required = false) Double width, @RequestParam(required = false) Double height, @RequestParam (required = false)Double scaleX,
                               @RequestParam(required = false) Double scaleY) {


        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(file.isEmpty() || x == null || y == null || width == null || scaleX == null || scaleY == null || height == null) { // Базовая валидация на стандартные параметры
            return "redirect:/profile/settings?emptyFile=true";
        }
        try {
            BufferedImage result = Utils.cropAnImage(x,y,scaleX,scaleY,ImageIO.read(file.getInputStream()),width,height); // Получить результирующую аватарку
            AvatarInfo theInfo = avatarInfoService.findByName(name).orElse(null); // Получить аватарку юзера, иначе null
            if(theInfo == null) { // Если аватарки нету, создаём новую, связываем с юзернеймом
                theInfo = new AvatarInfo();
                theInfo.setUsername(name);
            }
            String format = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1); // Получить формат картинки
            JsonNode node = new ObjectMapper().readTree(restService.convertToWebp(format,Utils.encodeAvatar(Utils.convertBufferedImageToBytes(result,format)))); // Конвертация картинки в WEBP
            theInfo.setAvatar(Utils.decodeAvatar(node.path("base64String").asText())); // Перевести картинку в формат base64String
            avatarInfoService.save(theInfo);
        }

        catch (IOException exception) {
            log.error(exception.toString()); // Залогировать ошибку
        }

        return "redirect:profile/" + name;
    }

    /**
     * Метод, отвечающий за отправку комментария
     * @param commentDTO - data transfer object - объект для транспортировки данных комментария
     */
    @MessageMapping("/sendComment") // /application/sendComment
    public void send(CommentDTO commentDTO) {
        commentDTO.setRating(0); // Новому комментарию ставим рейтинг 0
        commentDTO.setStringDate(Utils.formatDate(new Date(commentDTO.getDate()))); // Ставим комментарию актуальную дату
        var temp = Utils.toComment(commentDTO,userService); // Получаем объект Comment
        commentService.save(temp);
        commentDTO.setId(temp.getId());
        messagingTemplate.convertAndSendToUser(commentDTO.getRecipient(),"/queue/messages",commentDTO); // Используем STOMP для отправки комментария
    }

    /**
     * По какой-то причине получаю ошибку когда  JACKSON пытается десериализовать данные из JS(JSON.stringify({"id":id,"username":username,"like": true})) в две строки (public void plusLike(String username,String id,boolean like) {...})
     * Это временное решение
     **/
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
     * Метод отвечающий за изменение кол-во лайков у комментария (дизлайков).
     * @param  data - data transfer object - объкет транспортировки данных для сущности Like
     */
    @MessageMapping("/changeLikeAmount") //
    public void changeLikeAmount(LikeDTO data) {
        String username = data.username; // Получаем юзернейм пользователя, сделавший какое-то действие с комментарием
        Long parsedId = Long.parseLong(data.id);
        Comment comment = commentService.findById(parsedId); // Получение комментария по Id
        CommentAction action = commentActionService.findAction(parsedId,username); // Получение сущности "ДЕЙСТВИЕ" с комментарием по ID и username
        if(action == null)
            action = new CommentAction(parsedId,username, (byte)-1); // Если сущности не существует, тогда создать новую
        Byte theAction = action.getAction(); // Байт, обозначающий действие
        boolean isLike = data.isLike();
        if(isLike) // Если пользователь поставил лайк
            comment.setRating(comment.getRating() + (theAction == -1 ? 1 : theAction == 0 ? 2 : -1)); // Рейтинг + 1, если байт действия равен -1 (не был установлен ранее), Рейтинг + 2, если раньше был дизлайк, иначе рейтинг -1 (если раньше стоял лайк)
        else // Если дизлайк
            comment.setRating(comment.getRating() + (theAction == -1 ? -1 : theAction == 1 ? -2 : 1)); // Рейтинг - 1, если байт действия не установлен, Рейтинг + 1, если раньше был дизлайк, Рейтинг -2, если раньше был лайк
        commentService.save(comment);
        action.setAction((byte) (isLike ?  1 : 0)); // Если лайк - байт действия равен 1, если дизлайк, тогда байт действия равен 0, если не задан, то равен -1
        if(isLike) {
            if (theAction == 1) // Если сейчас поставлен лайк, и раньше тоже был лайк, тогда удаляем из БД (ничего по сути не было)
                commentActionService.delete(action);
            else // Если сейчас поставлен лайк, и раньше был поставлен НЕ лайк, тогда сохраняем из БД
                commentActionService.save(action);
        } else {
            if(theAction == 0) // Если раньше был дизлайк, и сейчас тоже дизлайк, удаляем из БД (ничего по сути не было)
                commentActionService.delete(action);
            else // Если сейчас поставлен дизлайк, и раньше был поставлен лайк, тогда сохраняем из БД
                commentActionService.save(action);
        }

        messagingTemplate.convertAndSendToUser(comment.getReceiver().getName(),"/queue/changeLikeAmount",parsedId + " " + comment.getRating());
    }

    /**
     * Метод для получения новых комментариев через AJAX
     * @param user - юзернейм пользователя, у которого будем считывать комментарии
     * @param currentPage - текущая страница в пагинации
     * @param usersWhoseAvatarsAreLoaded - пользователи, чьи аватарки уже загружены на сайт
     * @return
     */
    @GetMapping("/ajax/nextComments")
    public @ResponseBody Container<List<Comment>, HashMap<String, AvatarInfo>> getNextComments(String user, int currentPage,String usersWhoseAvatarsAreLoaded) {
        currentPage = Math.max(currentPage, 1); // Текущая страница в пагинации
        User receiver = userService.findByNameIgnoreCase(user); // Получатель комментариев
        String[] array = usersWhoseAvatarsAreLoaded.split(","); // Массив пользователей, чьи аватарки загружать не нужно т.к. они были загружены ранее
        var temp = initializeJSCL(receiver,currentPage,8,null,Arrays.asList(array));
        temp.t = Utils.reversedView(temp.t);
        return temp;
    }
    /**
     * Метод обработки добавления/удаления пользователя из друзей
     * @param name - юзернейм пользователя, которого мы удаляем/добавляем в друзья
     * @param isAFriend - является ли данный пользователь другом для текущего пользвателя
     * @return JSP страница профиля
     */
    @PostMapping("/friendAction")
    public String processFriendAction(@RequestParam(name = "username") String name, boolean isAFriend) {
        if(!isAFriend) {
            userService.addFriend(name,SecurityContextHolder.getContext().getAuthentication().getName());
        } else {
            userService.deleteFriend(name,SecurityContextHolder.getContext().getAuthentication().getName());
        }
        return "redirect:profile/" + name;
    }

    /**
     * Метод для обработки ajax запроса на отображение следующих друзей
     * @param user - пользователь, чьих друзей мы смотрим
     * @param currentPage - текущая страница пагинации
     * @param usersWhoseAvatarsAreLoaded - юзеры, чьи аватарки уже загружены, соотв. их аватарки не нужно загружать повторно
     * @return infos - набор всех новых аватарок, которые требуются для отображения на сайте соответсвенно с именем пользователей
     */
    @GetMapping("/ajax/nextFriends")
    public @ResponseBody List<AvatarInfo> getNextFriends(String user, int currentPage,String usersWhoseAvatarsAreLoaded) {
        currentPage = Math.max(currentPage, 1);
        List<String> array = Arrays.asList(usersWhoseAvatarsAreLoaded.split(","));
        var friends = userService.findAllFriendsReturnPage(userService.findByNameIgnoreCase(user),currentPage,6).getContent(); // Пагинация
        List<AvatarInfo> infos = new ArrayList<>(); // Аватарки друзей, которые нужно загрузить будут храниться тут
        for(User tempUser : friends) { // Для каждого друга, если его username не содежрится в списке на исключение (ава уже загружена) загружаме аватарку.
            if(!array.contains(tempUser.getName()))
                infos.add(avatarInfoService.findByNameOrReturnEmpty(tempUser.getName()));
        }
        return infos;
    }

    /**
     *  Метод обработки ajax запроса на онлайн пользователя (последнюю активность)
     * @param username - юзернейм пользователя, которого мы проверяем на онлайн
     * @return находится ли юзер в сети (на сайте в данный момент)
     */
    @GetMapping("/ajax/isUserAlive")
    public @ResponseBody Boolean isUserAlive(String username) {
        return userUtils.isAlive(username);
    }

    /**
     * Метод показа страницы настроек
     * @return JSP страница настроек
     */
    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // Получение пользователя (юз)
        username = StringUtils.capitalize(username.toLowerCase());
        User user = userService.findByNameIgnoreCase(username);
        if (user == null) {
            return "error/404";
        } // Если пользователь не найден возвращаем страницу с ошибкой.
        HashMap<String,AvatarInfo> avatars = new HashMap<>();
        model.addAttribute("name",username);
        model.addAttribute("email",user.getEmail());
        model.addAttribute("active",userUtils.isAlive(username));
        userUtils.loadAvatar(username,avatars,model,"my");
        model.addAttribute("avatars",avatars);
        model.addAttribute("hasToActivateEmail",user.getEnabled() == 1 ? 0 : 1);
        return "profile/settings";
    }
}
