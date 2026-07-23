package x10.trainup.coupon.infra.adapters.repositories.mapper;

import x10.trainup.commons.domain.entities.CouponEntity;
import x10.trainup.coupon.infra.datasources.mongo.CouponDocument;

import java.util.UUID;

public class CouponMapper {

    public static CouponEntity toEntity(CouponDocument doc) {
        if (doc == null) return null;
        return CouponEntity.builder()
                .id(doc.getId())
                .code(doc.getCode())
                .type(doc.getType())
                .value(doc.getValue())
                .minOrderAmount(doc.getMinOrderAmount())
                .maxDiscount(doc.getMaxDiscount())
                .usageLimit(doc.getUsageLimit())
                .usageCount(doc.getUsageCount())
                .startDate(doc.getStartDate())
                .endDate(doc.getEndDate())
                .isActive(doc.getIsActive())
                .isPublic(doc.getIsPublic() != null ? doc.getIsPublic() : true)
                .createdAt(doc.getCreatedAt())
                .build();
    }

    public static CouponDocument toDocument(CouponEntity entity) {
        if (entity == null) return null;
        return CouponDocument.builder()
                .id(entity.getId() != null ? entity.getId() : UUID.randomUUID().toString())
                .code(entity.getCode())
                .type(entity.getType())
                .value(entity.getValue())
                .minOrderAmount(entity.getMinOrderAmount())
                .maxDiscount(entity.getMaxDiscount())
                .usageLimit(entity.getUsageLimit())
                .usageCount(entity.getUsageCount())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive())
                .isPublic(entity.getIsPublic() != null ? entity.getIsPublic() : true)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
