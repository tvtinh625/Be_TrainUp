package x10.trainup.mailbox.infra.adapters;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import x10.trainup.mailbox.core.ports.MailSenderPort;

import java.io.UnsupportedEncodingException;

@Component
public class MailSenderAdapter implements MailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${SPRING_MAIL_FROM:${spring.mail.username:trainup18@gmail.com}}")
    private String mailFrom;

    public MailSenderAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(mailFrom, "TrainUp Life");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            System.out.println("✅ Email đã được gửi tới: " + to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("❌ Lỗi khi gửi email: " + e.getMessage());
        }
    }
}