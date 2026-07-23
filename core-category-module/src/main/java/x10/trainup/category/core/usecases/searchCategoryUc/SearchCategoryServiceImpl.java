package x10.trainup.category.core.usecases.searchCategoryUc;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.commons.domain.entities.CategoryEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class SearchCategoryServiceImpl implements ISearchCategoryUc {

    private final ICategoryReposity categoryReposity;

    @Override
    public List<CategoryEntity> searchCategories(String ten) {
        return categoryReposity.searchCategoriesByName(ten);
    }
}

