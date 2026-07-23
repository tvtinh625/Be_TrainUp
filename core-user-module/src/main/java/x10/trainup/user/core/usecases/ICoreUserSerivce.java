    package x10.trainup.user.core.usecases;


    import x10.trainup.commons.domain.entities.UserEntity;
    import x10.trainup.user.core.usecases.changePasswordUc.ChangePasswordReq;
    import x10.trainup.user.core.usecases.createGuestUserUc.CreateGuestUserReq;
    import x10.trainup.user.core.usecases.createUser.CreateUserReq;
    import x10.trainup.user.core.usecases.createUserWithoutFirebaseUc.CreateUserFireBaseReq;
    import x10.trainup.user.core.usecases.updatePasswordByEmail.UpdatePasswordByEmailReq;
    import x10.trainup.user.core.usecases.updateStatusUserUc.UpdateUserStatusReq;
    import x10.trainup.user.core.usecases.updateUser.UpdateUserReq;

    import java.util.List;
    import java.util.Optional;


    public interface ICoreUserSerivce {
        UserEntity createUser(CreateUserReq user);
        boolean existsById(String userId);
        UserEntity createUserWithoutFirebase(CreateUserFireBaseReq user);
        Optional<UserEntity> getUserById(String userId);
        UserEntity updateUser(String userId, UpdateUserReq user);

        void changePassword(String userId, ChangePasswordReq req);
        UserEntity markEmailVerified(String userId);
        boolean isEmailVerified(String userId);
        List<UserEntity> getAllUsers();
        void deleteUser(String userId);
        Optional<UserEntity> getUserByEmail(String email);
        void updatePasswordByEmail(UpdatePasswordByEmailReq req);
        UserEntity createGuestUser(CreateGuestUserReq req);

        UserEntity updateUserStatus(String userId, UpdateUserStatusReq req);
    }
