package mutata.com.github.MatematixProject.util;

import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.dto.CommentDTO;
import mutata.com.github.MatematixProject.service.MyService;
import mutata.com.github.MatematixProject.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * <h2>Утилитарный класс {@code Utils}</h2>
 * <p>Содержит статические вспомогательные методы для:
 * <ul>
 *   <li>конвертации и кодирования строк и дат;</li>
 *   <li>кодирования и декодирования изображений в Base64;</li>
 *   <li>обрезки и конвертации {@link BufferedImage};</li>
 *   <li>форматирования и парсинга дат по единому шаблону;</li>
 *   <li>реализации логики пагинации и сортировки для MVC-контроллеров;</li>
 *   <li>получения обратного представления списков без копирования;</li>
 *   <li>преобразования DTO комментария {@link CommentDTO} в сущность {@link Comment}.</li>
 * </ul>
 * Все методы безопасны для многопоточного доступа, кроме изменения
 * приватного поля {@link #dateFormat}, которое рекомендуется не менять в рантайме.
 * </p>
 */
public class Utils {

    /**
     * Формат даты для представления: "d MMM, yyyy H:mm".
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy H:mm");

    /**
     * Преобразует строку из ISO-8859-1 в UTF-8, чтобы корректно отобразить
     * текст, полученный из внешних источников в старой кодировке.
     *
     * @param content исходная строка в ISO-8859-1
     * @return новая строка в UTF-8
     * @throws NullPointerException если передан null
     */
    public static String encodeFromIsoToUTF8(String content) {
        return new String(content.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * Кодирует байтовый массив изображения в строку Base64 без символов паддинга,
     * пригодную для вставки в HTML/CSS как data URI.
     *
     * @param data массив байтов изображения (не null)
     * @return Base64-представление данных
     * @throws IllegalArgumentException если data == null
     */
    public static String encodeAvatar(byte[] data) {
        return Base64.getEncoder().withoutPadding().encodeToString(data);
    }

    /**
     * Декодирует строку Base64 (с данными изображения) обратно в байтовый массив.
     *
     * @param base64 строка в Base64 (не null, без padding)
     * @return декодированный массив байтов
     * @throws IllegalArgumentException если base64 == null
     */
    public static byte[] decodeAvatar(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Обрезает заданную область исходного изображения с учётом коэффициентов масштабирования.
     *
     * @param x      координата X левого верхнего угла до масштабирования
     * @param y      координата Y левого верхнего угла до масштабирования
     * @param scaleX масштаб по X (например, 2.0 если изображение в 2 раза больше)
     * @param scaleY масштаб по Y
     * @param src    исходное изображение (не null)
     * @param width  ширина области после масштабирования (>0)
     * @param height высота области (>0)
     * @return обрезанный {@link BufferedImage}
     * @throws IllegalArgumentException если src == null или width/height <=0
     */
    public static BufferedImage cropAnImage(double x, double y, double scaleX, double scaleY, BufferedImage src, double width, double height) {
        return src.getSubimage((int) (x * scaleX), (int) (y * scaleY), (int) width, (int) height);
    }

    /**
     * Конвертирует BufferedImage в массив байтов заданного формата.
     *
     * @param image  изображение для конвертации
     * @param format расширение/формат (например "png", "jpg")
     * @return массив байтов изображения или null при ошибке
     */
    public static byte[] convertBufferedImageToBytes(BufferedImage image, String format) {
        byte[] bytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            bytes = baos.toByteArray();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return bytes;
    }

    /**
     * Форматирует Date в строку по шаблону dateFormat.
     *
     * @param date объект Date
     * @return отформатированная строка
     */
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Парсит строку в объект Date по шаблону dateFormat.
     *
     * @param string строка с датой
     * @return объект Date
     * @throws ParseException при неверном формате строки
     */
    public static Date parseDate(String string) throws ParseException {
        return dateFormat.parse(string);
    }


    /**
     * Общая логика пагинации: добавляет параметры в модель и выполняет service.find...
     * @param token CSRF токен
     * @param model модель MVC
     * @param sortBy поле сортировки
     * @param findBy поле поиска
     * @param currentPage текущая страница
     * @param itemsPerPage элементы на странице
     * @param find строка поиска
     * @param sortDirection направление сортировки
     * @param service сервис для поиска и пагинации
     */
    public static  <T> void sharedLogicForPagination(
            CsrfToken token,
            Model model,
            String sortBy,
            String findBy,
            Integer currentPage,
            Integer itemsPerPage,
            String find,
            String sortDirection,
            MyService<T> service) {
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("findBy", findBy);
        model.addAttribute("csrfToken", token.getToken());
        sortBy = "noSort".equals(sortBy) ? null : sortBy;
        pagination(service, itemsPerPage, currentPage, sortBy, find, findBy, sortDirection, model);
    }

    /**
     * Выполняет пагинацию, поиск и сортировку сущностей в MVC-контроллере.
     * <p>Метод рассчитывает текущую страницу и размер страницы по умолчанию,
     * добавляет в модель необходимые атрибуты (currentPage, itemsPerPage, totalEntities),
     * затем в зависимости от наличия параметров поиска (findBy) и сортировки (sortBy)
     * вызывает соответствующие методы сервиса:
     * <ul>
     *     <li>Если {@code sortBy} пустой и {@code findBy} пустой — {@code findAllReturnPage}.</li>
     *     <li>Если {@code sortBy} пустой и {@code findBy} задан — {@code find(...)}.</li>
     *     <li>Если {@code sortBy} задан и {@code findBy} пустой — {@code findAllSortedBy(...)}.</li>
     *     <li>Если оба заданы — {@code findAndSort(...)}.</li>
     * </ul>
     * В результате в модель добавляются:
     * <ul>
     *     <li>{@code objects} — список сущностей для отображения;</li>
     *     <li>{@code total} — общее число страниц или записей для построения навигации;</li>
     *     <li>{@code sortDirection} — текущее направление сортировки.</li>
     * </ul>
     *
     * @param service       сервис {@link MyService} для работы с сущностями (не null)
     * @param itemsPerPage  число элементов на странице (nullable — по умолчанию 15)
     * @param currentPage   номер текущей страницы 1-based (nullable — по умолчанию 1)
     * @param sortBy        имя поля для сортировки (nullable)
     * @param find          строка поиска (nullable)
     * @param findBy        имя поля для поиска (nullable)
     * @param sortDirection направление сортировки: "asc" или "desc" (nullable — по умолчанию "asc")
     * @param model         MVC-модель для передачи атрибутов представления (не null)
     * @param <T>           тип обрабатываемых сущностей
     */
    public static <T> void pagination(
            MyService<T> service,
            Integer itemsPerPage,
            Integer currentPage,
            String sortBy,
            String find,
            String findBy,
            String sortDirection,
            Model model) {
        currentPage = (currentPage == null || currentPage <= 0) ? 1 : currentPage;
        itemsPerPage = (itemsPerPage == null) ? 15 : itemsPerPage;
        sortDirection = (sortDirection == null) ? "asc" : sortDirection;
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("totalEntities", service.getCount());
        Page<T> page;
        if (sortBy == null || sortBy.isEmpty()) {
            if (findBy == null || findBy.isEmpty()) {
                page = service.findAllReturnPage(currentPage - 1, itemsPerPage);
                model.addAttribute("objects", page.getContent());
                model.addAttribute("total", page.getTotalPages());
            } else {
                MyResponse<T> response = service.find(currentPage - 1, itemsPerPage, find, findBy);
                model.addAttribute("objects", response.getContent());
                model.addAttribute("total", (int) Math.ceil(response.getTotal() / (itemsPerPage * 1.0)));
            }
        } else {
            if (findBy == null || findBy.isEmpty()) {
                page = service.findAllSortedBy(currentPage - 1, itemsPerPage, sortBy, sortDirection);
                model.addAttribute("objects", page.getContent());
                model.addAttribute("total", page.getTotalPages());
            } else {
                MyResponse<User> response = service.findAndSort(currentPage - 1, itemsPerPage, find, findBy, sortBy, sortDirection);
                model.addAttribute("objects", response.getContent());
                model.addAttribute("total", (int) Math.ceil(response.getTotal() / (itemsPerPage * 1.0)));
            }
        }
        model.addAttribute("sortDirection", sortDirection);
    }


    /**
     * Вспомогательный класс-контейнер для AJAX-ответов (список + дополнительные данные).
     *
     * @param <T> тип основного списка
     * @param <R> тип дополнительных данных
     */
    public static class Container<T, R> {
        public T t;
        public R r;

        public Container(T t, R r) {
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

    /**
     * Возвращает "перевернутое" представление списка без копирования данных.
     *
     * @param list исходный список
     * @param <T>  тип элементов списка
     * @return список, обходящий элементы в обратном порядке
     */
    public static <T> List<T> reversedView(final List<T> list) {
        return new AbstractList<>() {
            @Override
            public T get(int index) {
                return list.get(list.size() - 1 - index);
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }

    /**
     * Преобразует DTO комментария в сущность Comment.
     *
     * @param commentDTO объект DTO с данными комментария
     * @param service    сервис UserService для поиска получателя
     * @return новая сущность Comment
     */
    public static Comment toComment(CommentDTO commentDTO, UserService service) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setRating(commentDTO.getRating());
        comment.setReceiver(service.findByNameIgnoreCase(commentDTO.getRecipient()));
        comment.setAuthor(commentDTO.getUsername());
        try {
            comment.setDate(new Date(commentDTO.getDate()));
        } catch (Exception exception) {
            exception.printStackTrace();
            comment.setDate(new Date());
        }
        return comment;
    }
}