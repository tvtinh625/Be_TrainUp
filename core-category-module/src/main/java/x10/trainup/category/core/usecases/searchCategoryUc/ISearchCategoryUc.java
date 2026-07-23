package x10.trainup.category.core.usecases.searchCategoryUc;

import x10.trainup.commons.domain.entities.CategoryEntity;

import java.util.List;

public interface ISearchCategoryUc {

    List<CategoryEntity> searchCategories(String ten);
}
