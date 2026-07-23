package x10.trainup.auth.core.usecases.signInUc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.user.core.repositories.IUserRepository;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SignInImpl implements ISignInUC {

    private final IUserRepository iUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService iJwtService;

    private final long accessTokenExpirationMs = 3600_000;

    @Override
    public SignInResp process(SignInReq req) {
        log.info("Processing sign in for email: {}", req.getEmail());

        // 1. Tìm user theo email
        UserEntity user = iUserRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> {
                    log.warn("Sign in failed: Email not found - {}", req.getEmail());
                    return new BusinessException(
                            UserError.EMAIL_NOT_FOUND,
                            "Email not found",
                            Map.of("email", req.getEmail())
                    );
                });

        // 2. Kiểm tra password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Sign in failed: Invalid password for user - {}", user.getId());
            throw new BusinessException(
                    UserError.INVALID_PASSWORD,
                    "Invalid password",
                    Map.of("email", req.getEmail())
            );
        }

        // 3. Kiểm tra trạng thái xác minh email
        if (!user.isEmailVerified()) {
            log.warn("Sign in failed: Email not verified - {}", user.getId());
            throw new BusinessException(
                    UserError.EMAIL_NOT_VERIFIED,
                    "Email not verified. Please verify your email first.",
                    Map.of(
                            "userId", user.getId(),
                            "email", user.getEmail()
                    )
            );
        }

        // 4. Kiểm tra trạng thái tài khoản theo UserStatus
        validateUserStatus(user);

        // 5. Sinh token
        String accessToken = iJwtService.generateAccessToken(user);
        String refreshToken = iJwtService.generateRefreshToken(user);

        log.info("Sign in successful for user: {}", user.getId());

        // 6. Trả về response (BAO GỒM ROLES)
        return SignInResp.builder()
                .id(user.getId())
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshExpiresIn(accessTokenExpirationMs)
                .refreshToken(refreshToken)
                .roles(user.getRoles())
                .build();
    }

    /**
     * Validate user status trước khi cho phép đăng nhập
     * @param user UserEntity cần validate
     * @throws BusinessException nếu status không hợp lệ
     */
    private void validateUserStatus(UserEntity user) {
        UserStatus status = user.getStatus();
        String userId = user.getId();

        switch (status) {
            case PENDING:
                log.warn("Sign in failed: Account pending verification - {}", userId);
                throw new BusinessException(
                        UserError.ACCOUNT_NOT_VERIFIED,
                        "Account is pending verification. Please complete account setup.",
                        Map.of(
                                "userId", userId,
                                "status", status.name()
                        )
                );

            case INACTIVE:
                log.warn("Sign in failed: Account inactive - {}", userId);
                throw new BusinessException(
                        UserError.ACCOUNT_DISABLED,
                        "Your account has been temporarily disabled. Please contact support.",
                        Map.of(
                                "userId", userId,
                                "status", status.name()
                        )
                );

            case BLOCKED:
                log.warn("Sign in failed: Account blocked - {}", userId);
                throw new BusinessException(
                        UserError.ACCOUNT_DISABLED,
                        "Your account has been blocked. Please contact support for assistance.",
                        Map.of(
                                "userId", userId,
                                "status", status.name(),
                                "supportEmail", "support@trainup.com" // optional
                        )
                );

            case ACTIVE:
                // ✅ Cho phép đăng nhập
                log.debug("User status validation passed: ACTIVE - {}", userId);
                break;

            default:
                log.error("Sign in failed: Unknown user status - {} for user {}", status, userId);
                throw new BusinessException(
                        UserError.USER_INACTIVE,
                        "Account status is invalid. Please contact support.",
                        Map.of(
                                "userId", userId,
                                "status", status.name()
                        )
                );
        }
    }
}