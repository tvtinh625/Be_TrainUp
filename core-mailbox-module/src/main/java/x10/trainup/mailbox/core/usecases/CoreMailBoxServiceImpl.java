package x10.trainup.mailbox.core.usecases;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.mailbox.core.usecases.confirmEmailUc.EmailVerifyResp;
import x10.trainup.mailbox.core.usecases.confirmEmailUc.IConfirmEmailUc;
import x10.trainup.mailbox.core.usecases.sendForgotPasswordOtpUc.ISendForgotPasswordOtpUc;
import x10.trainup.mailbox.core.usecases.sendForgotPasswordOtpUc.SendForgotPasswordOtpReq;
import x10.trainup.mailbox.core.usecases.sendVerificationEmailUc.sendVerificationEmailReq;
import x10.trainup.mailbox.core.usecases.sendVerificationEmailUc.SendVerificationEmailUc;

@Service
@AllArgsConstructor
public class CoreMailBoxServiceImpl implements ICoreMailBoxService {

    private final IConfirmEmailUc iConfirmEmailUc;
    private final SendVerificationEmailUc sendVerificationEmailUc;
    private final ISendForgotPasswordOtpUc iSendForgotPasswordOtpUc;
    @Override
    public void sendVerificationEmail(sendVerificationEmailReq req) {
        sendVerificationEmailUc.proccess(req);
    }

    @Override
    public EmailVerifyResp confirmEmail(String token) {
        return  iConfirmEmailUc.process(token);
    }

    @Override
    public void resendVerificationEmail(sendVerificationEmailReq req) {
        sendVerificationEmailUc.proccess(req);
    }
    @Override
    public void sendForgotPasswordOtp(SendForgotPasswordOtpReq req) {
        iSendForgotPasswordOtpUc.execute(req);
    }

}
