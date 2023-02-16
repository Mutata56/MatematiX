package mutata.com.github.MatematixProject.util.mail;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
@Data
public class JavaMailServiceImpl  {

    private static final String FROM = "noreply@MatematiX.com";

    @Autowired
    private JavaMailSender emailSender;
    public void send(String to,String subject,String text) {
        class SenderThread implements Runnable {
            private final SimpleMailMessage message;
            public SenderThread(SimpleMailMessage message) {
                this.message = message;
            }
            @Override
            public void run() {
                emailSender.send(message);
            }
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(FROM);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        (new Thread(new SenderThread(mailMessage))).start();
    }



}
