package mutata.com.github.MatematixProject.event;

import lombok.Getter;
import lombok.Setter;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.context.ApplicationEvent;

public class OnResetPasswordEvent extends ApplicationEvent {
    @Getter
    @Setter
    private User user;
    @Getter
    private final String url;
    public OnResetPasswordEvent(User user, String url) {
        super(user);
        this.url = url;
        this.user = user;
    }
}
