package x10.trainup.mailbox.core.usecases.sendForgotPasswordOtpUc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendForgotPasswordOtpReq {
    private String email;
    private String otp;
    private String subject;
}
