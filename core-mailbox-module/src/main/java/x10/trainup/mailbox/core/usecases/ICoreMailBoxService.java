package x10.trainup.mailbox.core.usecases;

import x10.trainup.mailbox.core.usecases.confirmEmailUc.EmailVerifyResp;
import x10.trainup.mailbox.core.usecases.sendForgotPasswordOtpUc.SendForgotPasswordOtpReq;
import x10.trainup.mailbox.core.usecases.sendVerificationEmailUc.sendVerificationEmailReq;

public interface ICoreMailBoxService {


    // 1. Gửi mail xác thực
    void sendVerificationEmail(sendVerificationEmailReq req);

    // 2. Xác nhận khi user click link trong mail
    EmailVerifyResp confirmEmail(String token);


    void resendVerificationEmail(sendVerificationEmailReq req);

    void sendForgotPasswordOtp(SendForgotPasswordOtpReq req);

}
