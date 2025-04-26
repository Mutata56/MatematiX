package mutata.com.github.MatematixProject.rest;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель ответа с деталями ошибки для REST-клиентов.
 * Используется в {@link GlobalExceptionHandler}
 * для передачи статуса, сообщения и времени возникновения ошибки.
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
public class UserErrorResponse {

    /**
     * HTTP-статус ответа (например, 404, 500).
     */
    private int status;

    /**
     * Описание ошибки или сообщение исключения.
     */
    private String message;

    /**
     * Метка времени (в миллисекундах) момента формирования ответа.
     */
    private long timeStamp;

}