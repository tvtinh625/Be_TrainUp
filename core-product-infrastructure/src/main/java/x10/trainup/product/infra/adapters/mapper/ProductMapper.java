package x10.trainup.product.infra.adapters.mapper;


import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.product.infra.datascoures.mongodb.ProductDocument;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductDocument toDocument(ProductEntity entity) {
        if (entity == null) return null;
        return ProductDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .categoryId(entity.getCategoryId())
                .brand(entity.getBrand())
                .active(true) // Mặc định active khi tạo, hoặc có thể set từ entity nếu có field này
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .sizes(entity.getSizes() != null
                        ? entity.getSizes().stream()
                        .map(SizeVariantMapper::toDocument)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    public static ProductEntity toEntity(ProductDocument doc) {
        if (doc == null) return null;
        return ProductEntity.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .categoryId(doc.getCategoryId())
                .brand(doc.getBrand())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .sizes(doc.getSizes() != null
                        ? doc.getSizes().stream()
                        .map(SizeVariantMapper::toEntity)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
