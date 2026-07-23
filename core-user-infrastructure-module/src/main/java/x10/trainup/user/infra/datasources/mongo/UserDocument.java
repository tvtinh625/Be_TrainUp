package x10.trainup.user.infra.datasources.mongo;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import x10.trainup.commons.domain.enums.AuthProvider;
import x10.trainup.commons.domain.enums.UserStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class UserDocument {
    @Id
    private String id;
    private String username;
    private String password;
    private String avatarUrl;
    private String coverUrl;

    private String avatarPublicId;
    private String coverPublicId;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true, sparse = true) // ✅ để null vẫn hợp lệ (vì Google có thể không có phone)
    private String phone;

    private UserStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private AddressDocument address;

    private List<RoleDocument> roles = new ArrayList<>();

    private boolean emailVerified;
    private LocalDateTime verifiedAt;

    // ✅ Thêm 2 trường mới để phân biệt loại tài khoản
    private AuthProvider provider;  // LOCAL, GOOGLE, FACEBOOK...
    private String providerId;      // UID Firebase / sub Google

    // Loyalty fields
    private x10.trainup.commons.domain.enums.UserTier tier;
    private Long loyaltyPoints;
    private java.math.BigDecimal totalSpent;
}
