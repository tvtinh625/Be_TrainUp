package x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.datasources.redis.ITokenStore;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.errors.UserError;

@Service
@AllArgsConstructor
public class VerifyForgotPasswordOtpUcImpl  implements IVerifyForgotPasswordOtpUc{


    private final ITokenStore tokenStore;
    private final IJwtService jwtService;

    @Override
    public VerifyForgotPasswordOtpRes process(String recoveryToken, VerifyForgotPasswordOtpReq req) {
        String email = jwtService.validateRecoveryToken(recoveryToken);

        String key = "forgot_otp:" + email;
        String storedOtp = tokenStore.getToken(key);
        System.out.println(" test storedOtp: " + storedOtp);

        if (storedOtp == null) {
            throw new BusinessException(UserError.OTP_EXPIRED, "Mã OTP đã hết hạn hoặc không tồn tại");
        }

        if (!storedOtp.equals(req.getOtp())) {
            throw new BusinessException(UserError.OTP_INVALID, "Mã OTP không chính xác");
        }

        // Xóa OTP cũ
        tokenStore.deleteToken(key);

        // Sinh resetToken mới để reset password
        String resetToken = jwtService.generateResetPasswordToken(email);

        return new VerifyForgotPasswordOtpRes(resetToken);
    }

}
