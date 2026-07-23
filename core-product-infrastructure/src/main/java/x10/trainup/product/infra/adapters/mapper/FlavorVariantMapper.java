package x10.trainup.product.infra.adapters.mapper;



import x10.trainup.commons.domain.entities.FlavorVariantEntity;
import x10.trainup.product.infra.datascoures.mongodb.FlavorVariantDocument;

public class FlavorVariantMapper {

    public static FlavorVariantDocument toDocument(FlavorVariantEntity entity) {
        if (entity == null) return null;
        return FlavorVariantDocument.builder()
                .id(entity.getId())
                .flavor(entity.getFlavor())
                .quantityInStock(entity.getQuantityInStock())
                .quantitySold(entity.getQuantitySold())
                .active(entity.isActive())
                .build();
    }

    public static FlavorVariantEntity toEntity(FlavorVariantDocument doc) {
        if (doc == null) return null;
        return FlavorVariantEntity.builder()
                .id(doc.getId())
                .flavor(doc.getFlavor())
                .quantityInStock(doc.getQuantityInStock())
                .quantitySold(doc.getQuantitySold())
                .active(doc.isActive())
                .build();
    }
}
