package x10.trainup.auth.core.usecases.ResetPasswordUc;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.user.core.usecases.updatePasswordByEmail.UpdatePasswordByEmailReq;

@Service
@AllArgsConstructor
public class ResetPasswordUcImpl implements IResetPasswordUc {

    private final ICoreUserSerivce userService;
    private final IJwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(String resetToken, ResetPasswordReq req) {
        String email = jwtService.validateResetPasswordTokenAndGetEmail(resetToken);
        if (email == null || email.isEmpty()) {
            throw new BusinessException(UserError.TOKEN_INVALID, "Token đặt lại mật khẩu không hợp lệ hoặc đã hết hạn");
        }
        var user = userService.getUserByEmail(email)
                .orElseThrow(() -> new BusinessException(UserError.EMAIL_NOT_FOUND, "Không tìm thấy người dùng tương ứng với email"));
        String hashedPassword = passwordEncoder.encode(req.getNewPassword());
        userService.updatePasswordByEmail(
                UpdatePasswordByEmailReq.builder()
                        .email(user.getEmail())
                        .newPassword(hashedPassword)
                        .build()
        );
    }
}
