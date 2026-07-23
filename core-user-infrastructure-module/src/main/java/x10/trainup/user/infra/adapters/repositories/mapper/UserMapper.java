package x10.trainup.user.infra.adapters.repositories.mapper;

import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.user.infra.datasources.mongo.UserDocument;
import x10.trainup.commons.domain.enums.AuthProvider;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserMapper {

    public static UserDocument toDocument(UserEntity entity) {
        if (entity == null) return null;

        UserDocument doc = UserDocument.builder()
                .id(entity.getId() != null ? entity.getId() : UUID.randomUUID().toString())
                .username(entity.getUsername())
                .avatarUrl(entity.getAvatarUrl())
                .coverUrl(entity.getCoverUrl())
                .coverPublicId(entity.getCoverPublicId())
                .avatarPublicId(entity.getAvatarPublicId())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .address(AddressMapper.toDocument(entity.getAddress()))
                .roles(RoleMapper.toDocuments(entity.getRoles()))
                .emailVerified(entity.isEmailVerified())
                .verifiedAt(entity.getVerifiedAt())
                // Map mới: provider & providerId
                .provider(entity.getProvider() != null ? entity.getProvider() : AuthProvider.LOCAL)
                .providerId(entity.getProviderId())
                .tier(entity.getTier() != null ? entity.getTier() : x10.trainup.commons.domain.enums.UserTier.BRONZE)
                .loyaltyPoints(entity.getLoyaltyPoints() != null ? entity.getLoyaltyPoints() : 0L)
                .totalSpent(entity.getTotalSpent() != null ? entity.getTotalSpent() : java.math.BigDecimal.ZERO)
                .build();

        // Nếu roles null hoặc empty thì set mặc định CUSTOMER
        if (doc.getRoles() == null || doc.getRoles().isEmpty()) {
            doc.setRoles(List.of(
                    new x10.trainup.user.infra.datasources.mongo.RoleDocument(
                            UUID.randomUUID().toString(),
                            x10.trainup.commons.domain.enums.Role.CUSTOMER,
                            Set.of()
                    )
            ));
        }
        return doc;
    }

    public static UserEntity toEntity(UserDocument doc) {
        if (doc == null) return null;
        return UserEntity.builder()
                .id(doc.getId())
                .username(doc.getUsername())
                .avatarUrl(doc.getAvatarUrl())
                .coverUrl(doc.getCoverUrl())
                .avatarPublicId(doc.getAvatarPublicId())
                .coverPublicId(doc.getCoverPublicId())
                .password(doc.getPassword())
                .email(doc.getEmail())
                .phone(doc.getPhone())
                .status(doc.getStatus())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .address(AddressMapper.toEntity(doc.getAddress()))
                .roles(RoleMapper.toEntities(doc.getRoles()))
                .emailVerified(doc.isEmailVerified())
                .verifiedAt(doc.getVerifiedAt())
                // Map mới: provider & providerId
                .provider(doc.getProvider() != null ? doc.getProvider() : AuthProvider.LOCAL)
                .providerId(doc.getProviderId())
                .tier(doc.getTier() != null ? doc.getTier() : x10.trainup.commons.domain.enums.UserTier.BRONZE)
                .loyaltyPoints(doc.getLoyaltyPoints() != null ? doc.getLoyaltyPoints() : 0L)
                .totalSpent(doc.getTotalSpent() != null ? doc.getTotalSpent() : java.math.BigDecimal.ZERO)
                .build();
    }
}
