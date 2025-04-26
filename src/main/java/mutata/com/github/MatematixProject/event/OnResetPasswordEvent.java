package mutata.com.github.MatematixProject.event;

import lombok.Getter;
import lombok.Setter;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.context.ApplicationEvent;

/**
 * Событие, генерируемое при запросе сброса пароля пользователя.
 * <p>Используется для инициирования процесса создания и отправки
 * ссылки на смену пароля на Email пользователя.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see org.springframework.context.ApplicationEvent
 */
public class OnResetPasswordEvent extends ApplicationEvent {

    /**
     * Пользователь, для которого создаётся событие сброса пароля.
     * <p>Содержит данные для последующей ассоциации с токеном сброса.</p>
     */
    @Getter @Setter
    private User user;

    /**
     * Базовый URL или контекстный путь приложения,
     * используемый для формирования полной ссылки сброса пароля.
     */
    @Getter
    private final String url;

    /**
     * Создаёт новое событие запроса сброса пароля.
     *
     * @param user пользователь, запросивший смену пароля
     * @param url  базовый URL приложения (контекстный путь),
     *             к которому будет добавлен токен сброса
     */
    public OnResetPasswordEvent(User user, String url) {
        super(user);
        this.url = url;
        this.user = user;
    }
}
