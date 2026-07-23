package x10.trainup.category.core.repositories;

import x10.trainup.commons.domain.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface ICategoryReposity {

    Optional<CategoryEntity> findByName(String name);

    CategoryEntity save(CategoryEntity category);

    List<CategoryEntity> findAll();

    Optional<CategoryEntity> findById(String id);

    List<CategoryEntity> searchCategoriesByName(String name);

    boolean existsById(String id);

    void deleteById(String id);
}
