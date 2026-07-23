package x10.trainup.category.core.usecases.deleteCategoryUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.category.core.errors.CategoryError;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.commons.ports.IProductQueryService;

@Service
@AllArgsConstructor
public class DeleteCategoryImpl implements IDeleteCategoryUc {

    private final ICategoryReposity iCategoryReposity;
    private final IProductQueryService iProductQueryService;

    @Override
    public void process(String categoryId) {
        // ✅ 1. Kiểm tra tồn tại
        var categoryOpt = iCategoryReposity.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new BusinessException(CategoryError.CATEGORY_NOT_FOUND);
        }

        // ✅ 2. Kiểm tra xem category có sản phẩm nào không
        boolean hasProducts = iProductQueryService.existsByCategoryId(categoryId);
        if (hasProducts) {
            throw new BusinessException(CategoryError.CATEGORY_IN_USE);
        }

        // ✅ 3. Xoá category
        iCategoryReposity.deleteById(categoryId);
    }
}
