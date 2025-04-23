package mutata.com.github.MatematixProject.event;

import lombok.Getter;
import lombok.Setter;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.context.ApplicationEvent;

/**
 * Класс, описывающий событие, создаваемое по запросу смены пароля.
 */
public class OnResetPasswordEvent extends ApplicationEvent {
    /**
     * Юзер, владеющий токеном
     */
    @Getter
    @Setter
    private User user;

    /**
     * Формируемая ссылка, по которой будет проходить пользователь, чтобы подтвердить свою почту.
     */
    @Getter
    private final String url;

    
    public OnResetPasswordEvent(User user, String url) {
        super(user);
        this.url = url;
        this.user = user;
    }
}

