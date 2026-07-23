package x10.trainup.mailbox.core.ports;

public interface MailSenderPort {
    void send(String to, String subject, String body);

    void sendHtml(String to, String subject, String htmlBody); // thêm cho html
}
