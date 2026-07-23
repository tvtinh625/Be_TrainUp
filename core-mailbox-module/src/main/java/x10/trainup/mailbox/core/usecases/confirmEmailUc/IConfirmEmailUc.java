package x10.trainup.mailbox.core.usecases.confirmEmailUc;

public interface IConfirmEmailUc {
    EmailVerifyResp process(String token);
}
