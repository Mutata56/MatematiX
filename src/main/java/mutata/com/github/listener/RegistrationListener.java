package mutata.com.github.listener;

import mutata.com.github.entity.User;
import mutata.com.github.event.OnRegistrationCompleteEvent;
import mutata.com.github.service.VerificationTokenService;
import mutata.com.github.util.JavaMailSenderWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final VerificationTokenService service;
    private final JavaMailSenderWrapper mailSender;

    @Autowired
    public RegistrationListener(VerificationTokenService service, JavaMailSenderWrapper mailSender) {
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
