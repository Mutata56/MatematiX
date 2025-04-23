package mutata.com.github.MatematixProject.event;

import lombok.Getter;
import lombok.Setter;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.context.ApplicationEvent;

/**
 * Класс, описывающий событие, создаваемое по завершению регистрации, и нужное для подтверждения почты.
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    /**
     * Формируемая ссылка, по которой будет проходить пользователь, чтобы подтвердить свою почту.
     */
    @Getter
    private final String url;
    /**
     * Юзер, владеющий токеном
     */
    @Getter
    @Setter
    private User user;

    public OnRegistrationCompleteEvent(User user, String url) {
        super(user);
        this.url = url;
        this.user = user;
    }
}
