package x10.trainup.user.core.usecases.updatePasswordByEmail;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.repositories.IUserRepository;

@Service
@AllArgsConstructor
public class UpdatePasswordByEmailImlp implements IUpdatePasswordByEmailUc{
    private final IUserRepository userRepository;
    @Override
    public void process(UpdatePasswordByEmailReq req) {

        UserEntity user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException(UserError.EMAIL_NOT_FOUND, "Không tìm thấy người dùng"));
        user.setPassword(req.getNewPassword());
        userRepository.save(user);
    }

}
