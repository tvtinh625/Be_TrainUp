package x10.trainup.category.infra.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.category.infra.adapters.repositories.mapper.CategoryMapper;
import x10.trainup.category.infra.datascoures.mongoDb.CategoryDocument;
import x10.trainup.category.infra.datascoures.mongoDb.CategoryMongoRepository;
import x10.trainup.commons.domain.entities.CategoryEntity;



import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements ICategoryReposity {

    private final CategoryMongoRepository categoryMongoRepository;

    @Override
    public CategoryEntity save(CategoryEntity category) {
        // Chuyển từ Entity sang Document rồi lưu vào MongoDB
        CategoryDocument doc = CategoryMapper.toDocument(category);
        CategoryDocument saved = categoryMongoRepository.save(doc);
        return CategoryMapper.toEntity(saved);
    }

    @Override
    public Optional<CategoryEntity> findByName(String name) {
        // Có thể dùng repository hoặc MongoTemplate đều được
        Optional<CategoryDocument> optionalDoc = categoryMongoRepository.findByName(name);
        return optionalDoc.map(CategoryMapper::toEntity);
    }

    @Override
    public List<CategoryEntity> findAll() {
        List<CategoryDocument> docs = categoryMongoRepository.findAll();
        return docs.stream()
                .map(CategoryMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoryEntity> findById(String id) {
        return categoryMongoRepository.findById(id)
                .map(CategoryMapper::toEntity);
    }

    @Override
    public List<CategoryEntity> searchCategoriesByName(String name) {
        return categoryMongoRepository.searchByNameContainsIgnoreCase(name).stream()
                .map(CategoryMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String id) {
        return categoryMongoRepository.existsById(id);
    }
    @Override
    public void deleteById(String id) {
        categoryMongoRepository.deleteById(id);
    }
}
