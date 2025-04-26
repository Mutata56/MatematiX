
package mutata.com.github.MatematixProject.listener;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.service.VerificationTokenService;
import mutata.com.github.MatematixProject.util.mail.JavaMailServiceImpl;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Слушатель событий завершения регистрации пользователя.
 * <p>Обрабатывает {@link OnRegistrationCompleteEvent}, создаёт
 * токен верификации и отправляет письмо с ссылкой подтверждения.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see OnRegistrationCompleteEvent
 * @see VerificationTokenService
 * @see JavaMailServiceImpl
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    /**
     * Сервис для управления токенами подтверждения email.
     */
    private final VerificationTokenService service;

    /**
     * Сервис для отправки email-сообщений.
     */
    private final JavaMailServiceImpl mailSender;

    /**
     * Создаёт слушатель с внедрением необходимых сервисов.
     *
     * @param service    сервис управления токенами подтверждения
     * @param mailSender сервис отправки email-сообщений
     */
    public RegistrationListener(VerificationTokenService service, JavaMailServiceImpl mailSender) {
        this.service = service;
        this.mailSender = mailSender;
    }

    /**
     * Обработка события завершения регистрации.
     * <p>Вызывает внутренний метод confirmRegistration.</p>
     *
     * @param event событие завершения регистрации
     */
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        confirmRegistration(event);
    }

    /**
     * Основная логика подтверждения регистрации:
     * <ol>
     *   <li>Генерация уникального токена подтверждения.</li>
     *   <li>Сохранение токена через {@link VerificationTokenService}.</li>
     *   <li>Формирование URL для подтверждения.</li>
     *   <li>Отправка email с инструкциями и ссылкой.</li>
     * </ol>
     *
     * @param event событие с пользователем и базовым URL
     */
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        // Сохраняем токен в БД
        service.createVerificationToken(user, token);
        // Формируем ссылку для подтверждения регистрации
        String confirmationUrl = "http://localhost:8080" + event.getUrl()
                + "/auth/registrationConfirm?token=" + token;
        // Отправляем письмо с токеном (действует 24 часа)
        mailSender.send(
                user.getEmail(),
                "MatematiX - Подтверждение регистрации",
                "Ссылка истечёт через 24 часа\r\n" + confirmationUrl
        );
    }
}