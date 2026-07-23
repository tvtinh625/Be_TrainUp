package x10.trainup.category.core.usecases;

import x10.trainup.category.core.usecases.createCategoryUc.CreateCategoryReq;
import x10.trainup.category.core.usecases.updateCategoryUc.UpdateCategoryReq;
import x10.trainup.commons.domain.entities.CategoryEntity;

import java.util.List;

public interface ICoreCategoryService {

     CategoryEntity createCategory (CreateCategoryReq category);

     List<CategoryEntity> getAllCategories ();

     CategoryEntity updateCategory(String categoryId, UpdateCategoryReq req);


     List<CategoryEntity> searchCategories(String ten);

     // ✅ Thêm hàm xoá
     void deleteCategory(String categoryId);

}
