package x10.trainup.auth.core.usecases.ResetPasswordUc;

public interface IResetPasswordUc {

    void execute(String resetToken,ResetPasswordReq req);

}
