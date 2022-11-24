package mutata.com.github.MatematixProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

@Component
public class JavaMailServiceImpl {

    public static final String FROM = "noreply@MatematiX.com";

    @Autowired
    private JavaMailSender emailSender;

    public MimeMessage createMimeMessage() {
        return emailSender.createMimeMessage();
    }

    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return emailSender.createMimeMessage(contentStream);
    }

    public void send(MimeMessage mimeMessage) throws MailException {
        emailSender.send(mimeMessage);
    }

    public void send(MimeMessage... mimeMessages) throws MailException {
        emailSender.send(mimeMessages);
    }

    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        emailSender.send(mimeMessagePreparator);
    }


    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
        emailSender.send(mimeMessagePreparators);
    }


    public void send(SimpleMailMessage simpleMessage) throws MailException {
        emailSender.send(simpleMessage);
    }


    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        emailSender.send(simpleMessages);
    }

    public void send(String from,String to,String subject,String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        send(mailMessage);
    }
    public void send(String to,String subject,String text) {
        send(FROM,to,subject,text);
    }
}
