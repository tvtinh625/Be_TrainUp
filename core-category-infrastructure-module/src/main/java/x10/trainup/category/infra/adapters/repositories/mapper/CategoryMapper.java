package x10.trainup.category.infra.adapters.repositories.mapper;
import x10.trainup.category.infra.datascoures.mongoDb.CategoryDocument;
import x10.trainup.commons.domain.entities.CategoryEntity;

public class CategoryMapper {


    public static CategoryDocument toDocument(CategoryEntity entity) {
        if (entity == null) return null;
        return CategoryDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .build();
    }


    public static CategoryEntity toEntity(CategoryDocument document) {
        if (document == null) return null;
        return CategoryEntity.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .active(document.isActive())
                .build();
    }
}
