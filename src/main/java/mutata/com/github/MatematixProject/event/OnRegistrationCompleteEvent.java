package mutata.com.github.MatematixProject.event;

import lombok.Getter;
import lombok.Setter;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.context.ApplicationEvent;


public class OnRegistrationCompleteEvent extends ApplicationEvent {
    @Getter
    private final String url;
    @Getter
    @Setter
    private User user;

    public OnRegistrationCompleteEvent(User user, String url) {
        super(user);
        this.url = url;
        this.user = user;
    }
}
