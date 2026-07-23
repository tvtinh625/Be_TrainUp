package x10.trainup.user.core.usecases;


import lombok.AllArgsConstructor;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.user.core.repositories.IUserRepository;
import x10.trainup.user.core.usecases.changePasswordUc.ChangePasswordReq;
import x10.trainup.user.core.usecases.changePasswordUc.IChangePasswordUc;
import x10.trainup.user.core.usecases.createGuestUserUc.CreateGuestUserReq;
import x10.trainup.user.core.usecases.createGuestUserUc.ICreateGuestUserUc;
import x10.trainup.user.core.usecases.createUser.CreateUserReq;
import x10.trainup.user.core.usecases.createUser.ICreateUserUc;
import org.springframework.stereotype.Service;
import x10.trainup.user.core.usecases.createUserWithoutFirebaseUc.ICreateUserWithoutFirebase;
import x10.trainup.user.core.usecases.updatePasswordByEmail.IUpdatePasswordByEmailUc;
import x10.trainup.user.core.usecases.updatePasswordByEmail.UpdatePasswordByEmailReq;
import x10.trainup.user.core.usecases.updateStatusUserUc.IUpdateUserStatusUc;
import x10.trainup.user.core.usecases.updateStatusUserUc.UpdateUserStatusReq;
import x10.trainup.user.core.usecases.updateUser.IUpdateUserUc;
import x10.trainup.user.core.usecases.updateUser.UpdateUserReq;


import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class CoreUserServiceImpl implements ICoreUserSerivce{

    private final IUserRepository userRepository;
    private final ICreateUserUc createUserUC;
    private final IUpdateUserUc updateUserUC;
    private  final IUserRepository repository;
    private final IChangePasswordUc changePasswordUc;
    private final ICreateUserWithoutFirebase createUserWithoutFirebaseUc;
    private final IUpdatePasswordByEmailUc updatePasswordByEmailUc;
    private  final ICreateGuestUserUc iCreateGuestUserUc;
    private final IUpdateUserStatusUc iUpdateUserStatusUc;

    @Override
    public UserEntity updateUserStatus(String userId, UpdateUserStatusReq req){
        return iUpdateUserStatusUc.process(userId,req);
    }

    @Override
    public UserEntity createUserWithoutFirebase(x10.trainup.user.core.usecases.createUserWithoutFirebaseUc.CreateUserFireBaseReq req) {
        return createUserWithoutFirebaseUc.process(req);
    }

    @Override
    public UserEntity createUser(CreateUserReq req) {
        return createUserUC.process(req);
    }


    @Override
    public Optional<UserEntity> getUserById(String userId) {
        return repository.findById(userId);
    }

    @Override
    public UserEntity updateUser(String userId, UpdateUserReq user) {
       return updateUserUC.process(userId,user);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity markEmailVerified(String userId) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        UserEntity user = optionalUser.get();
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);
        return userRepository.save(user);
    }


    @Override
    public boolean isEmailVerified(String userId) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        UserEntity user = optionalUser.get();
        return user.getStatus() == UserStatus.ACTIVE;
    }

    @Override
    public void deleteUser(String userId) {
        System.out.print(" ok ok ");

    }

    @Override
    public void changePassword(String userId, ChangePasswordReq req) {
        changePasswordUc.execute(userId,req);
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updatePasswordByEmail(UpdatePasswordByEmailReq req) {
        updatePasswordByEmailUc.process(req);
    }

    @Override
    public boolean existsById(String userId){
        return  userRepository.existsById(userId);
    }

    @Override
    public UserEntity createGuestUser(CreateGuestUserReq req) {
        return  iCreateGuestUserUc.execute(req);
    }
}
