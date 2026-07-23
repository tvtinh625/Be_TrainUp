package x10.trainup.product.infra.adapters.mapper;



import x10.trainup.commons.domain.entities.SizeVariantEntity;
import x10.trainup.product.infra.datascoures.mongodb.SizeVariantDocument;
import java.util.stream.Collectors;

public class SizeVariantMapper {

    public static SizeVariantDocument toDocument(SizeVariantEntity entity) {
        if (entity == null) return null;
        return SizeVariantDocument.builder()
                .id(entity.getId())
                .size(entity.getSize())
                .price(entity.getPrice())
                .discountPrice(entity.getDiscountPrice())
                .imageUrl(entity.getImageUrl())
                .imageUrls(entity.getImageUrls())
                .weight(entity.getWeight())
                .flavors(entity.getFlavors() != null
                        ? entity.getFlavors().stream()
                        .map(FlavorVariantMapper::toDocument)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    public static SizeVariantEntity toEntity(SizeVariantDocument doc) {
        if (doc == null) return null;
        return SizeVariantEntity.builder()
                .id(doc.getId())
                .size(doc.getSize())
                .price(doc.getPrice())
                .discountPrice(doc.getDiscountPrice())
                .imageUrl(doc.getImageUrl())
                .imageUrls(doc.getImageUrls())
                .weight(doc.getWeight())
                .flavors(doc.getFlavors() != null
                        ? doc.getFlavors().stream()
                        .map(FlavorVariantMapper::toEntity)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
