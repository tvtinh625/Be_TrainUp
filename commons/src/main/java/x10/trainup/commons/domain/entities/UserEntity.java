package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.AuthProvider;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.commons.domain.enums.UserTier;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    private String id;
    private String username;
    private String coverUrl;
    private String avatarUrl;
    private String password;
    private String email;
    private String phone;
    private List<RoleEntity> roles;
    private UserStatus status;
    private AddressEntity address;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean emailVerified;
    private LocalDateTime verifiedAt;
    private AuthProvider provider; // GOOGLE, LOCAL, FACEBOOK, ...
    private String providerId;

    private String avatarPublicId;
    private String coverPublicId;

    // Loyalty fields
    private UserTier tier;
    private Long loyaltyPoints;
    private BigDecimal totalSpent;}



