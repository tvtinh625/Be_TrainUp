package x10.trainup.auth.core.usecases.forgotPassWordUc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import x10.trainup.commons.datasources.redis.ITokenStore;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.mailbox.core.usecases.ICoreMailBoxService;
import x10.trainup.mailbox.core.usecases.sendForgotPasswordOtpUc.SendForgotPasswordOtpReq;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.security.core.util.PasswordHashUtil;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.security.SecureRandom;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordUcImpl implements IForgotPasswordUc {

    private final ICoreUserSerivce userService;
    private final ICoreMailBoxService mailService;
    private final ITokenStore tokenStore;
    private final IJwtService jwtService;

    private static final long OTP_TTL_MS = 5 * 60 * 1000L; // 5 phút
    private static final long RESEND_TTL_MS = 60 * 1000L;
    private static final String OTP_PREFIX = "forgot_otp:"; // ✅ dùng đúng key giống Verify
    private static final String RESEND_PREFIX = "forgot_resend:";

    @Override
    public ForgotPasswordRes execute(ForgotPasswordReq req) {
        if (req == null || !StringUtils.hasText(req.getEmail())) {
            throw new BusinessException(UserError.INVALID_EMAIL, "Email không hợp lệ");
        }

        String email = req.getEmail().trim().toLowerCase();

        // ✅ 1. Kiểm tra user tồn tại
        Optional<UserEntity> userOpt = userService.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            throw new BusinessException(UserError.EMAIL_NOT_FOUND, "Email không tồn tại");
        }

        UserEntity user = userOpt.get();

        // ✅ 2. Kiểm tra trạng thái user
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(UserError.USER_INACTIVE, "Tài khoản chưa kích hoạt hoặc bị khóa");
        }

        // ✅ 3. Chống spam gửi OTP
        String resendKey = RESEND_PREFIX + email;
        if (tokenStore.getToken(resendKey) != null) {
            throw new BusinessException(UserError.OTP_REQUEST_TOO_SOON, "Vui lòng chờ 1 phút trước khi gửi lại OTP");
        }

        // ✅ 4. Sinh OTP và lưu Redis (hash lại để bảo mật)
        String otp = generateOtp();
        tokenStore.saveToken(OTP_PREFIX + email, otp, OTP_TTL_MS);
        tokenStore.saveToken(resendKey, "1", RESEND_TTL_MS);

        // ✅ 5. Tạo recoveryToken (JWT)
        String recoveryToken = jwtService.generateRecoveryToken(email);

        // ✅ 6. Gửi mail OTP
        mailService.sendForgotPasswordOtp(
                SendForgotPasswordOtpReq.builder()
                        .email(email)
                        .otp(otp)
                        .subject("TrainUp Life - Mã OTP đặt lại mật khẩu")
                        .build()
        );

        // ✅ 7. Trả response
        return ForgotPasswordRes.builder()
                .recoveryToken(recoveryToken)
                .email(email)
                .build();
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(random.nextInt(900_000) + 100_000);
    }
}
