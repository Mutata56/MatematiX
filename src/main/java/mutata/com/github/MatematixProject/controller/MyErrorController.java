package mutata.com.github.MatematixProject.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
/**
 * Контроллер сайта, связанный с обработкой ошибок.
 * @author Khaliullin Cyrill
 * @version 1.0.0
 *
 * Controller - данный класс является контроллером (предназначен для непосредственной обработки запросов от клиента и возвращения результатов).
 * RequestMapping - обрабытывает все запросы с префиксом "/error"
 */
@Controller
public class MyErrorController implements ErrorController {
    /**
     * Получение статуса ошибки и дальнейшая обработка
     * @param request - HTTP запрос
     * @return JSP страницу с соответсвующей ошибкой
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE); // Получаем статус ошибки

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString()); // Получаем числовое значение ошибки

            if(statusCode == HttpStatus.NOT_FOUND.value()) { // Ошибка 404 - страница не найдена

                return "error/404";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) { // Ошибка 500 - внутрненняя ошибка со стороны сервера
                return "error/500";
            }
        }
        return "error"; // Общая ошибка
    }
}
