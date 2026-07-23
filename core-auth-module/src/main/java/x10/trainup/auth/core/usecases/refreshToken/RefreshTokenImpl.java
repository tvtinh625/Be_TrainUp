package x10.trainup.auth.core.usecases.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.auth.core.errors.AuthError;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.repositories.IUserRepository;

@Service
@RequiredArgsConstructor
public class RefreshTokenImpl implements IRefreshTokenUc {

    private final IJwtService jwtService;
    private final IUserRepository userRepository;

    @Override
    public RefreshTokenResponse process(RefreshTokenRequest req) {
        try {
            // 1. Xác thực refresh token
            String userId = jwtService.validateToken(req.getToken());

            // 2. Tìm user
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(UserError.USER_NOT_FOUND, null));

            // 3. Tạo access token mới
            String newAccessToken = jwtService.generateAccessToken(user);

            // 4. (Optional) Tạo refresh token mới luôn nếu muốn rotation
            String newRefreshToken = jwtService.generateRefreshToken(user);
            return RefreshTokenResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();

        } catch (Exception e) {
            throw new BusinessException(AuthError.INVALID_REFRESH_TOKEN, null);
        }
    }
}
