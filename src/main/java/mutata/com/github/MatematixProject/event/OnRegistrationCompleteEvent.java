
package mutata.com.github.MatematixProject.event;

import lombok.Getter;
import lombok.Setter;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.context.ApplicationEvent;

/**
 * Событие, генерируемое после успешной регистрации пользователя.
 * <p>Используется для инициирования процесса отправки письма
 * с ссылкой подтверждения email.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see org.springframework.context.ApplicationEvent
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    /**
     * Базовый URL или путь приложения, к которому будет добавлен токен подтверждения.
     * <p>Используется для формирования полной ссылки подтверждения.</p>
     */
    @Getter
    private final String url;

    /**
     * Пользователь, для которого создаётся событие регистрации.
     * <p>Содержит данные для последующей ассоциации с токеном верификации.</p>
     */
    @Getter @Setter
    private User user;

    /**
     * Создаёт новое событие завершения регистрации.
     *
     * @param user клиентская сущность пользователя, зарегистрированная в системе
     * @param url  базовый URL приложения (контекстный путь),
     *             используемый для построения ссылки подтверждения
     */
    public OnRegistrationCompleteEvent(User user, String url) {
        super(user);
        this.url = url;
        this.user = user;
    }
}