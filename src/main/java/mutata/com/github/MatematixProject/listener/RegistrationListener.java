package mutata.com.github.MatematixProject.listener;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.service.VerificationTokenService;
import mutata.com.github.MatematixProject.util.mail.JavaMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final VerificationTokenService service;
    private  JavaMailServiceImpl mailSender;
    @Autowired
    public RegistrationListener(VerificationTokenService service, JavaMailServiceImpl mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user,token);
        String url = "http://localhost:8080" + event.getUrl() + "/auth/registrationConfirm?token=" + token;
        mailSender.send(user.getEmail(),"MatematiX - Подтверждение регистрации",
                "Ссылка истечет через 24 часа\r\n" + url);
    }

}
