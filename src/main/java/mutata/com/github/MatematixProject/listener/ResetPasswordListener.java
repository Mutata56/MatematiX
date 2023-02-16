package mutata.com.github.MatematixProject.listener;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.event.OnResetPasswordEvent;
import mutata.com.github.MatematixProject.service.ResetPasswordTokenService;
import mutata.com.github.MatematixProject.util.mail.JavaMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ResetPasswordListener implements ApplicationListener<OnResetPasswordEvent> {

    private JavaMailServiceImpl mailSender;
    private final ResetPasswordTokenService service;

    @Autowired
    public ResetPasswordListener(JavaMailServiceImpl mailSender,ResetPasswordTokenService service) {
        this.mailSender = mailSender;
        this.service = service;
    }

    @Override
    public void onApplicationEvent(OnResetPasswordEvent event) {
        process(event);
    }
    private void process(OnResetPasswordEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createResetPasswordToken(user,token);
        String url = "http://localhost:8080" + event.getUrl() + "/auth/resetPassword?token=" + token;
        mailSender.send(user.getEmail(),"MatematiX - Сброс пароля",
                "Ссылка истечет через 4 часа\r\n" + url);
    }
}
