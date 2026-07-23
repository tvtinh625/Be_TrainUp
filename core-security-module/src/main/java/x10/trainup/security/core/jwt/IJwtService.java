package x10.trainup.security.core.jwt;

import x10.trainup.commons.domain.entities.UserEntity;

public interface IJwtService {
    String generateToken(String userId, String username, String email);
    String validateToken(String token);

    String generateAccessToken(UserEntity user);
    String generateRefreshToken(UserEntity user);


    String generateRecoveryToken(String email);
    String validateRecoveryToken(String token);


    String generateResetPasswordToken(String email);
    String validateResetPasswordTokenAndGetEmail(String token);

    /**
     * Sinh JWT dùng 1 lần cho Guest đánh giá sản phẩm qua email.
     * Subject = orderId, claim type = guest-review
     */
    String generateGuestReviewToken(String orderId, String guestEmail);

    /**
     * Xác thực token đánh giá guest và trả về orderId (subject).
     * Null nếu token không hợp lệ hoặc sai type.
     */
    String validateGuestReviewToken(String token);

    /**
     * Sinh JWT dùng dài hạn để Guest theo dõi danh sách lịch sử đơn hàng qua email.
     * Subject = guestEmail, claim type = guest-history
     */
    String generateGuestHistoryToken(String guestEmail);

    /**
     * Xác thực token lịch sử guest và trả về email (subject).
     */
    String validateGuestHistoryToken(String token);
}
