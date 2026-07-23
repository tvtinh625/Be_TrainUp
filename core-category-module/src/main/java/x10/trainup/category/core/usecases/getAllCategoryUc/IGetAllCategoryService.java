package x10.trainup.category.core.usecases.getAllCategoryUc;

import x10.trainup.commons.domain.entities.CategoryEntity;

import java.util.List;

public interface IGetAllCategoryService {

    List<CategoryEntity> getAllCategories ();
}
