package mutata.com.github.MatematixProject.rest;

/**
 * Исключение, выбрасываемое при попытке найти пользователя,
 * которого нет в базе данных.
 * <p>Наследуется от {@link RuntimeException} и передаёт сообщение
 * о причинах возникновения ошибки.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Создаёт новое исключение с указанным сообщением.
     *
     * @param message описание причины, по которой пользователь не найден
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}