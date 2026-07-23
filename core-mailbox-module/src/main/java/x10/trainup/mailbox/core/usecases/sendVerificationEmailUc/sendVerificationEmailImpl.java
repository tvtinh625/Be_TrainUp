package x10.trainup.mailbox.core.usecases.sendVerificationEmailUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.mailbox.core.ports.MailSenderPort;

@Service
@AllArgsConstructor
public class sendVerificationEmailImpl implements SendVerificationEmailUc {

    private final MailSenderPort mailSenderPort;

    @Override
    public void proccess(sendVerificationEmailReq req) {
        String confirmLink = "http://localhost:8080/api/auth/verify-email?token=" + req.getToken();
        String subject = "Xác nhận email của bạn";
        String htmlBody = """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding:20px;">
              <div style="max-width:600px; margin:0 auto; background:#fff; padding:30px; border-radius:8px;">
                <h2 style="color:#3498db;">Xin chào %s,</h2>
                <p>Cảm ơn bạn đã đăng ký. Vui lòng nhấn vào nút bên dưới để xác nhận email:</p>
                <p style="text-align:center; margin:30px 0;">
                  <a href="%s" style="background:#3498db; color:white; padding:12px 24px; text-decoration:none; border-radius:6px; font-weight:bold;">
                    Xác nhận tài khoản
                  </a>
                </p>
                <p>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>
              </div>
            </body>
            </html>
            """.formatted(req.getUsername(), confirmLink);

        mailSenderPort.sendHtml(req.getEmail(), subject, htmlBody);
    }
}
