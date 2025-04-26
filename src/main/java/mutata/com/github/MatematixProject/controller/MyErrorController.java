package mutata.com.github.MatematixProject.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер для обработки ошибок на уровне всего приложения.
 * <p>Перехватывает все запросы по пути <code>/error</code> и возвращает
 * соответствующий шаблон в зависимости от HTTP-статуса.</p>
 *
 * <ul>
 *   <li>404 Not Found -> error/404</li>
 *   <li>500 Internal Server Error -> error/500</li>
 *   <li>Любой другой статус -> error</li>
 * </ul>
 *
 * NOTE: Реализация не изменяет поведение кода, только расширяет документацию.
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see org.springframework.boot.web.servlet.error.ErrorController
 */
@Controller
public class MyErrorController implements ErrorController {

    /**
     * Обрабатывает все ошибки приложения.
     * <p>Метод извлекает атрибут <code>ERROR_STATUS_CODE</code> из запроса,
     * приводя его к целому числу, после чего сравнивает с известными статусами:
     * <ul>
     *   <li>Если 404 — возвращает шаблон <code>error/404</code>,</li>
     *   <li>Если 500 — возвращает шаблон <code>error/500</code>,</li>
     *   <li>Во всех остальных случаях — общий шаблон <code>error</code>.</li>
     * </ul>
     *
     * @param request текущий HTTP-запрос, в котором хранится атрибут с кодом ошибки
     * @return имя JSP-страницы для отображения пользователю
     * @see HttpStatus#NOT_FOUND
     * @see HttpStatus#INTERNAL_SERVER_ERROR
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Извлечение кода ошибки из атрибутов запроса
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            // Преобразование к строке и числу для сравнения
            Integer statusCode = Integer.valueOf(status.toString());

            // Обработка 404 Not Found
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }
            // Обработка 500 Internal Server Error
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }
        }
        // Общая страница ошибки для прочих статусов
        return "error";
    }

    /**
     * Переопределение устаревшего метода getErrorPath не требуется в новых версиях Spring Boot.
     * Метод оставлен для совместимости, но фактически не используется.
     *
     * @deprecated С Spring Boot 2.3+ реализовывать интерфейс ErrorController достаточно только с методом @RequestMapping("/error").
     */
    @Deprecated
    public String getErrorPath() {
        return "/error";
    }
}