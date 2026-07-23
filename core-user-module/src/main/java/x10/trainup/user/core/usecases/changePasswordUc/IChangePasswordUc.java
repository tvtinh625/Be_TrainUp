package x10.trainup.user.core.usecases.changePasswordUc;

public interface IChangePasswordUc {
    void execute(String userId, ChangePasswordReq req);
}
