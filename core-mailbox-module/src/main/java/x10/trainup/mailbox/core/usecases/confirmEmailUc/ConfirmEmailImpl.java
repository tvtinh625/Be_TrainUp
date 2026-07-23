package x10.trainup.mailbox.core.usecases.confirmEmailUc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import x10.trainup.commons.datasources.redis.ITokenStore;
import x10.trainup.commons.domain.enums.EmailVerifyStatus;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

@Service
@RequiredArgsConstructor
public class ConfirmEmailImpl implements IConfirmEmailUc {

    private final IJwtService jwtService;
    private final ITokenStore tokenStore;
    private final ICoreUserSerivce userService;

    @Override
    public EmailVerifyResp process(String token) {
        try {
            String userId = jwtService.validateToken(token);

            System.out.println("UserId from token: " + userId); // Debugging line

            String storedToken = tokenStore.getToken("verify:" + userId);

            System.out.println("Stored token from Redis: " + storedToken); // Debugging line
            if (userService.isEmailVerified(userId)) {
                return new EmailVerifyResp(EmailVerifyStatus.ALREADY_VERIFIED, "Email đã được xác minh trước đó");
            }

            if (storedToken == null) {
                return new EmailVerifyResp(EmailVerifyStatus.EXPIRED, "Token đã hết hạn");
            }

            if (!storedToken.equals(token)) {
                return new EmailVerifyResp(EmailVerifyStatus.INVALID, "Token không hợp lệ");
            }

            userService.markEmailVerified(userId);
            tokenStore.deleteToken("verify:" + userId);

            return new EmailVerifyResp(EmailVerifyStatus.SUCCESS, "Xác minh thành công");

        } catch (Exception e) {
            return new EmailVerifyResp(EmailVerifyStatus.INVALID, "Token không hợp lệ");
        }
    }

}
