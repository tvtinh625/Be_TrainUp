package x10.trainup.mailbox.core.usecases.resendVerificationEmailUc;

import x10.trainup.mailbox.core.usecases.sendVerificationEmailUc.sendVerificationEmailReq;

public interface IResendVerificationEmailUc {
    void proccess(sendVerificationEmailReq req);
}
