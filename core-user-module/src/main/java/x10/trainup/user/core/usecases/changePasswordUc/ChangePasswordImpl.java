package x10.trainup.user.core.usecases.changePasswordUc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.repositories.IUserRepository;

@Service
@RequiredArgsConstructor
public class ChangePasswordImpl implements IChangePasswordUc {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(String userId, ChangePasswordReq req) {
        // ✅ 1. Tìm user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserError.USER_NOT_FOUND, null));

        // ✅ 2. Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BusinessException(UserError.INVALID_OLD_PASSWORD, null);
        }

        // ✅ 3. Không cho đổi nếu trùng mật khẩu cũ
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new BusinessException(UserError.SAME_AS_OLD_PASSWORD, null);
        }

        // ✅ 4. Cập nhật mật khẩu mới (hash)
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }
}
