package x10.trainup.category.core.usecases.getAllCategoryUc;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.commons.domain.entities.CategoryEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class GetAllCategoryServiceImpl implements  IGetAllCategoryService{


    private final ICategoryReposity categoryReposity;
    @Override
    public List<CategoryEntity>  getAllCategories() {
        return categoryReposity.findAll();
    }
}
