package mutata.com.github.MatematixProject.listener;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.event.OnResetPasswordEvent;
import mutata.com.github.MatematixProject.service.ResetPasswordTokenService;
import mutata.com.github.MatematixProject.util.mail.JavaMailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.UUID;
/**
 * Класс, прослушивающий события OnResetPasswordEvent.
 * @see OnResetPasswordEvent
 */
@Component
public class ResetPasswordListener implements ApplicationListener<OnResetPasswordEvent> {

    /**
     * Объкет для работы с почтой
     */

    private JavaMailServiceImpl mailSender;

    /**
     * Объкет для работы с БД resetPassword_tokens
     * @see mutata.com.github.MatematixProject.entity.token.ResetPasswordToken
     */

    private final ResetPasswordTokenService service;

    @Autowired
    public ResetPasswordListener(JavaMailServiceImpl mailSender,ResetPasswordTokenService service) {
        this.mailSender = mailSender;
        this.service = service;
    }

    /**
     * Метод, запускаемый, когда в системе создаётся OnResetPasswordEvent
     * @param event - событие OnResetPasswordEvent, которое было создано в системе
     */
    @Override
    public void onApplicationEvent(OnResetPasswordEvent event) {
        process(event);
    }

    /**
     * Метод основной логики при регистрации пользователяю.
     * @param event - событие OnResetPasswordEvent, которое было создано в системе
     */
    private void process(OnResetPasswordEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString(); // Генерация токена в виде строкового литерала
        service.createResetPasswordToken(user,token); // Сохранение токена
        String url = "http://localhost:8080" + event.getUrl() + "/auth/resetPassword?token=" + token;
        mailSender.send(user.getEmail(),"MatematiX - Сброс пароля",
                "Ссылка истечет через 4 часа\r\n" + url);
    }
}
