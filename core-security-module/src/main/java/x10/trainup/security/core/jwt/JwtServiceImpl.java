package x10.trainup.security.core.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.security.core.config.JwtProperties;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements IJwtService {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final long recoveryTokenExpirationMs;
    private final long resetPasswordTokenExpirationMs; // thêm TTL cho reset password token

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        this.accessTokenExpirationMs = jwtProperties.getAccessTokenExpiration();
        this.refreshTokenExpirationMs = jwtProperties.getRefreshTokenExpiration();
        // Giả định TTL là 5 phút (300,000 ms) hoặc có thể đọc từ JwtProperties nếu cần
        this.recoveryTokenExpirationMs = 5 * 60 * 1000L;
        this.resetPasswordTokenExpirationMs = 10 * 60 * 1000L; // 10 phút cho reset password
    }


    @Override
    public String generateToken(String userId, String username, String email) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public String validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }


    @Override
    public String generateAccessToken(UserEntity user) {
        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList()
                : List.of();

        List<String> permissions = user.getRoles() != null
                ? user.getRoles().stream()
                .filter(r -> r.getPermissions() != null)
                .flatMap(r -> r.getPermissions().stream())
                .map(Enum::name)
                .distinct()
                .toList()
                : List.of();

        return Jwts.builder()
                .setSubject(user.getId())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("roles", roleNames)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    @Override
    public String generateRecoveryToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + recoveryTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String validateRecoveryToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            // Subject của Recovery Token là Email
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // Token hết hạn
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            // Lỗi chung (chữ ký không hợp lệ, token sai cấu trúc)
            return null;
        }
    }

    public String generateResetPasswordToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "resetPassword")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + resetPasswordTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateResetPasswordTokenAndGetEmail(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (!"resetPassword".equals(claims.get("type", String.class))) return null;
            return claims.getSubject(); // trả về email
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String generateGuestReviewToken(String orderId, String guestEmail) {
        // Token có hạn 7 ngày — đủ để guest đánh giá sau khi nhận hàng
        long sevenDaysMs = 7 * 24 * 60 * 60 * 1000L;
        return Jwts.builder()
                .setSubject(orderId)
                .claim("type", "guest-review")
                .claim("email", guestEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + sevenDaysMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String validateGuestReviewToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (!"guest-review".equals(claims.get("type", String.class))) return null;
            return claims.getSubject(); // trả về orderId
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String generateGuestHistoryToken(String guestEmail) {
        // Token có hạn 30 ngày để guest theo dõi lịch sử
        long thirtyDaysMs = 30L * 24 * 60 * 60 * 1000L;
        return Jwts.builder()
                .setSubject(guestEmail)
                .claim("type", "guest-history")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + thirtyDaysMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String validateGuestHistoryToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (!"guest-history".equals(claims.get("type", String.class))) return null;
            return claims.getSubject(); // trả về email
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
