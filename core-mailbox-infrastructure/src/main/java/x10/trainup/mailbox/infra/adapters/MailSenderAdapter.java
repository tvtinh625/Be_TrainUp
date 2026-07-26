package x10.trainup.mailbox.infra.adapters;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import x10.trainup.mailbox.core.ports.MailSenderPort;

import java.util.List;
import java.util.Map;

@Component
public class MailSenderAdapter implements MailSenderPort {

    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;

    @Value("${SPRING_MAIL_FROM:${SPRING_MAIL_USERNAME:${spring.mail.username:quocthao2005@gmail.com}}}")
    private String mailFrom;

    @Value("${RESEND_API_KEY:}")
    private String resendApiKey;

    @Value("${BREVO_API_KEY:}")
    private String brevoApiKey;

    public MailSenderAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(String to, String subject, String body) {
        if (trySendViaHttp(to, subject, body, false)) {
            return;
        }
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
        if (trySendViaHttp(to, subject, htmlBody, true)) {
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(mailFrom, "TrainUp Life");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            System.out.println("✅ Email đã được gửi tới: " + to);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi gửi email qua SMTP: " + e.getMessage());
        }
    }

    private boolean trySendViaHttp(String to, String subject, String content, boolean isHtml) {
        if (resendApiKey != null && !resendApiKey.isBlank()) {
            return sendViaResend(to, subject, content, isHtml);
        }
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            return sendViaBrevo(to, subject, content, isHtml);
        }
        return false;
    }

    private boolean sendViaResend(String to, String subject, String content, boolean isHtml) {
        try {
            String url = "https://api.resend.com/emails";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey.trim());

            Map<String, Object> body = Map.of(
                    "from", "TrainUp Life <onboarding@resend.dev>",
                    "to", List.of(to),
                    "subject", subject,
                    isHtml ? "html" : "text", content
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ Email đã được gửi thành công qua Resend HTTPS API (Port 443) tới: " + to);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi email qua Resend API: " + e.getMessage());
            return false;
        }
    }

    private boolean sendViaBrevo(String to, String subject, String content, boolean isHtml) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey.trim());

            Map<String, Object> body = Map.of(
                    "sender", Map.of("name", "TrainUp Life", "email", mailFrom),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    isHtml ? "htmlContent" : "textContent", content
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ Email đã được gửi thành công qua Brevo HTTPS API (Port 443) tới: " + to);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi email qua Brevo API: " + e.getMessage());
            return false;
        }
    }
}