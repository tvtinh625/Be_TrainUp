package x10.trainup.category.core.usecases.updateCategoryUc;

import x10.trainup.commons.domain.entities.CategoryEntity;

public interface IUpdateCategoryService {

    CategoryEntity process(String categoryId ,UpdateCategoryReq req);
}

