
package mutata.com.github.MatematixProject.listener;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.event.OnResetPasswordEvent;
import mutata.com.github.MatematixProject.service.ResetPasswordTokenService;
import mutata.com.github.MatematixProject.util.mail.JavaMailServiceImpl;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Слушатель событий запроса сброса пароля.
 * <p>Обрабатывает событие {@link OnResetPasswordEvent}, генерирует
 * токен сброса пароля, сохраняет его и отправляет пользователю
 * письмо со ссылкой для смены пароля.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see OnResetPasswordEvent
 * @see ResetPasswordTokenService
 * @see JavaMailServiceImpl
 */
@Component
public class ResetPasswordListener implements ApplicationListener<OnResetPasswordEvent> {

    /**
     * Сервис для сохранения токена сброса пароля в БД.
     */
    private final ResetPasswordTokenService service;

    /**
     * Сервис для отправки email сообщений.
     */
    private final JavaMailServiceImpl mailSender;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param mailSender сервис отправки почты
     * @param service    сервис управления токенами сброса пароля
     */
    public ResetPasswordListener(JavaMailServiceImpl mailSender, ResetPasswordTokenService service) {
        this.mailSender = mailSender;
        this.service = service;
    }

    /**
     * Обработка события {@link OnResetPasswordEvent}.
     * <p>Вызывает метод <code>process</code> для выполнения логики сброса.</p>
     *
     * @param event событие запроса сброса пароля
     */
    @Override
    public void onApplicationEvent(OnResetPasswordEvent event) {
        process(event);
    }

    /**
     * Основная логика обработки события сброса пароля:
     * <ol>
     *   <li>Генерация уникального токена;</li>
     *   <li>Сохранение токена в базе данных;</li>
     *   <li>Формирование URL для смены пароля;</li>
     *   <li>Отправка email со ссылкой и информацией о сроке действия.</li>
     * </ol>
     *
     * @param event событие с данными пользователя и базовым URL
     */
    private void process(OnResetPasswordEvent event) {
        User user = event.getUser();
        // Генерация уникального строкового токена
        String token = UUID.randomUUID().toString();
        // Сохранение токена в БД через сервис
        service.createResetPasswordToken(user, token);

        // Формирование полной ссылки для сброса пароля
        String resetUrl = "http://localhost:8080" + event.getUrl()
                + "/auth/resetPassword?token=" + token;

        // Отправка письма пользователю с инструкциями и ссылкой
        mailSender.send(
                user.getEmail(),
                "MatematiX - Сброс пароля",
                "Ссылка истечет через 4 часа\r\n" + resetUrl
        );
    }
}