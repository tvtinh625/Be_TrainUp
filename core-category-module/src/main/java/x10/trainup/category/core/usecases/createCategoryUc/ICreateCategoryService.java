package x10.trainup.category.core.usecases.createCategoryUc;

import x10.trainup.commons.domain.entities.CategoryEntity;

public interface ICreateCategoryService {

    CategoryEntity process (CreateCategoryReq category);
}
