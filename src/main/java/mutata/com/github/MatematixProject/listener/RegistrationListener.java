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

/**
 * Класс, прослушивающий события OnRegistrationCompleteEvent.
 * @see OnRegistrationCompleteEvent
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    /**
     * Объкет для работы с БД verification_tokens
     * @see mutata.com.github.MatematixProject.entity.token.VerificationToken
     */
    private final VerificationTokenService service;

    /**
     * Объкет для работы с почтой
     */
    private  JavaMailServiceImpl mailSender;

    @Autowired
    public RegistrationListener(VerificationTokenService service, JavaMailServiceImpl mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    /**
     * Метод, запускаемый, когда в системе создаётся OnRegistrationCompleteEvent
     * @param event - событие OnRegistrationCompleteEvent, которое было создано в системе
     */
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        confirmRegistration(event);
    }

    /**
     * Метод основной логики при регистрации пользователяю.
     * @param event - событие OnRegistrationCompleteEvent, которое было создано в системе
     */
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString(); // Генерация токена в виде строкового литерала
        service.createVerificationToken(user,token); // Сохранение токена
        String url = "http://localhost:8080" + event.getUrl() + "/auth/registrationConfirm?token=" + token;
        mailSender.send(user.getEmail(),"MatematiX - Подтверждение регистрации",
                "Ссылка истечет через 24 часа\r\n" + url);
    }

}
