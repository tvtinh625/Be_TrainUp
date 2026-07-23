package x10.trainup.category.core.usecases.updateCategoryUc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.category.core.errors.CategoryError;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.commons.domain.entities.CategoryEntity;
import x10.trainup.commons.exceptions.BusinessException;

@Service
@RequiredArgsConstructor
public class UpdateCategoryImpl implements IUpdateCategoryService {

    private final ICategoryReposity categoryRepository;

    @Override
    public CategoryEntity process(String categoryId, UpdateCategoryReq req) {
        // Chuẩn hóa input
        req.normalize();

        // Lấy category hiện tại
        CategoryEntity existing = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CategoryError.CATEGORY_NOT_FOUND));

        // Kiểm tra trùng tên (loại trừ chính nó)
        if (req.getName() != null && !req.getName().isBlank()) {
            categoryRepository.findByName(req.getName().trim())
                    .ifPresent(other -> {
                        if (!other.getId().equals(existing.getId())) {
                            throw new BusinessException(CategoryError.CATEGORY_ALREADY_EXISTS);
                        }
                    });
            existing.setName(req.getName().trim());
        }

        // Cập nhật mô tả
        if (req.getDescription() != null) {
            existing.setDescription(req.getDescription().trim());
        }

        // ✅ Cập nhật trạng thái active
            existing.setActive(req.isActive());
        // Lưu lại DB
        return categoryRepository.save(existing);
    }
}
