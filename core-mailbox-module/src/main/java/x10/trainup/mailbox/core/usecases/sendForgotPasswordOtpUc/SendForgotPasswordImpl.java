package x10.trainup.mailbox.core.usecases.sendForgotPasswordOtpUc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.trainup.mailbox.core.ports.MailSenderPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendForgotPasswordImpl implements ISendForgotPasswordOtpUc {

    private final MailSenderPort mailSenderPort;

    @Override
    public void execute(SendForgotPasswordOtpReq req) {
        validateRequest(req);

        try {
            String html = buildEmailTemplate(req.getOtp());
            mailSenderPort.sendHtml(req.getEmail(), req.getSubject(), html);
            log.info("✅ OTP email đã được gửi đến {}", req.getEmail());
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi OTP email tới {}: {}", req.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Không thể gửi OTP email. Vui lòng thử lại sau!");
        }
    }

    private void validateRequest(SendForgotPasswordOtpReq req) {
        if (req.getEmail() == null || req.getEmail().isEmpty())
            throw new IllegalArgumentException("Email không được để trống");

        if (req.getOtp() == null || req.getOtp().length() != 6)
            throw new IllegalArgumentException("OTP phải có đúng 6 ký tự");
    }

    private String buildEmailTemplate(String otp) {
        return """
            <html>
              <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px;">
                  <h2 style="color: #2c3e50;">Yêu cầu đặt lại mật khẩu</h2>
                  <p>Xin chào,</p>
                  <p>Bạn vừa yêu cầu đặt lại mật khẩu cho tài khoản TrainUp Life. Đây là mã OTP của bạn:</p>
                  <h1 style="text-align:center; color:#007bff; letter-spacing:4px;">%s</h1>
                  <p style="text-align:center;">Mã OTP này sẽ hết hạn sau <b>5 phút</b>.</p>
                  <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>
                  <hr>
                  <p style="font-size: 12px; color: #888; text-align: center;">
                    TrainUp Life © 2025. Bảo mật tài khoản của bạn là ưu tiên hàng đầu của chúng tôi.
                  </p>
                </div>
              </body>
            </html>
        """.formatted(otp);
    }
}
