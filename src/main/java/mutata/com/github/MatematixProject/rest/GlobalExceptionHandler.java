package mutata.com.github.MatematixProject.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * <p>Перехватывает определённые исключения и формирует
 * корректный HTTP-ответ с телом {@link UserErrorResponse}.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link UserNotFoundException}.
     * <p>Формирует ответ со статусом 404 NOT_FOUND и
     * JSON-объектом {@link UserErrorResponse} с деталями ошибки.</p>
     *
     * @param exception исключение, вызвавшее обработку (пользователь не найден)
     * @return {@link ResponseEntity} с телом {@link UserErrorResponse}
     *         и HTTP-статусом 404
     */
    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(UserNotFoundException exception) {
        UserErrorResponse userErrorResponse = new UserErrorResponse();
        userErrorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        userErrorResponse.setMessage(exception.getMessage());
        userErrorResponse.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.NOT_FOUND);
    }
}