package mutata.com.github.listener;

import mutata.com.github.entity.User;
import mutata.com.github.event.OnRegistrationCompleteEvent;
import mutata.com.github.service.VerificationTokenService;
import mutata.com.github.util.JavaMailSenderWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
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
    private static class EmailSenderThread extends Thread {

        private final OnRegistrationCompleteEvent event;
        private final RegistrationListener registrationListener;
        private EmailSenderThread(OnRegistrationCompleteEvent event,RegistrationListener registrationListener) {
            this.event = event;
            this.registrationListener = registrationListener;
        }

        @Override
        public void run() {
            User user = event.getUser();
            String token = UUID.randomUUID().toString();
            registrationListener.service.createVerificationToken(user,token);
            String url = "http://localhost:8080" + event.getUrl() + "/auth/registrationConfirm?token=" + token;
            registrationListener.mailSender.send(user.getEmail(),"MatematiX - Подтверждение регистрации",
                    "Ссылка истечет через 24 часа\r\n" + url);
        }
    }
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        EmailSenderThread thread = new EmailSenderThread(event,this);
        thread.start();
    }

}
