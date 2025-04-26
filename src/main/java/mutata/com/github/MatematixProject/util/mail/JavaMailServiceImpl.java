package mutata.com.github.MatematixProject.util.mail;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Реализация сервиса отправки простых email-сообщений.
 * <p>Отвечает за асинхронную отправку писем через Spring {@link JavaMailSender}.</p>
 *
 * <p>Используется для уведомлений пользователей о подтверждении регистрации,
 * сбросе пароля и других системных событиях.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Component
@Data  // Генерирует геттеры/сеттеры, toString, equals, hashCode
public class JavaMailServiceImpl {

    /**
     * Email-адрес отправителя, отображаемый в поле "From" письма.
     */
    private static final String FROM = "kkrll56@yandex.ru";

    /**
     * Бин Spring для отправки email-сообщений.
     */
    @Autowired
    private JavaMailSender emailSender;

    /**
     * Отправляет простое текстовое сообщение на указанный адрес.
     * <p>Создает поток для асинхронной отправки,
     * чтобы не блокировать текущий поток обработки.</p>
     *
     * @param to      адрес электронной почты получателя
     * @param subject тема письма
     * @param text    текстовое содержимое письма
     */
    public void send(String to, String subject, String text) {
        // Вложенный класс для выполнения отправки в отдельном потоке
        class SenderThread implements Runnable {
            private final SimpleMailMessage message;

            /**
             * Конструктор для настройки сообщения.
             *
             * @param message предварительно сформированное письмо
             */
            public SenderThread(SimpleMailMessage message) {
                this.message = message;
            }

            /**
             * Отправка email через {@link JavaMailSender}.
             */
            @Override
            public void run() {
                emailSender.send(message);
            }
        }

        // Подготовка простого текстового сообщения
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(FROM);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        // Запуск отправки в отдельном потоке
        new Thread(new SenderThread(mailMessage)).start();
    }
}
