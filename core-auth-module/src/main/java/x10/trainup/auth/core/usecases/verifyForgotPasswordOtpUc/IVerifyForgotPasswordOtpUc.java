package x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc;

public interface IVerifyForgotPasswordOtpUc {

    VerifyForgotPasswordOtpRes process(String recoveryToken,VerifyForgotPasswordOtpReq req);
}
