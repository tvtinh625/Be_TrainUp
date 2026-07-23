package x10.trainup.category.core.usecases.createCategoryUc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.category.core.errors.CategoryError;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.commons.domain.entities.CategoryEntity;
import x10.trainup.commons.exceptions.BusinessException;

@Service
@RequiredArgsConstructor
public class CreateCategoryImpl implements ICreateCategoryService {

    private final ICategoryReposity categoryRepository;

    @Override
    public CategoryEntity process(CreateCategoryReq req) {
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new BusinessException(CategoryError.CATEGORY_NAME_REQUIRED);
        }
        String normalizedName = req.getName().trim();
        categoryRepository.findByName(normalizedName)
                .ifPresent(existing -> {
                    throw new BusinessException(CategoryError.CATEGORY_ALREADY_EXISTS);
                });
        CategoryEntity entity = CategoryEntity.builder()
                .name(normalizedName)
                .description(req.getDescription())
                .active(req.isActive())
                .build();

        return categoryRepository.save(entity);
    }
}
