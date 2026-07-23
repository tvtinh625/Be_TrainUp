package x10.trainup.category.core.usecases;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.category.core.usecases.createCategoryUc.CreateCategoryReq;
import x10.trainup.category.core.usecases.createCategoryUc.ICreateCategoryService;
import x10.trainup.category.core.usecases.deleteCategoryUc.IDeleteCategoryUc;
import x10.trainup.category.core.usecases.getAllCategoryUc.IGetAllCategoryService;
import x10.trainup.category.core.usecases.searchCategoryUc.ISearchCategoryUc;
import x10.trainup.commons.domain.entities.CategoryEntity;
import x10.trainup.category.core.usecases.updateCategoryUc.IUpdateCategoryService;
import x10.trainup.category.core.usecases.updateCategoryUc.UpdateCategoryReq;

import java.util.List;

@Service
@AllArgsConstructor
public class CoreCategoryServiceImpl implements  ICoreCategoryService {

    private final ICreateCategoryService createCategoryService;
    private final IGetAllCategoryService getAllCategoryService;
    private final IUpdateCategoryService updateCategoryService;
    private final ISearchCategoryUc searchCategoryUc;
    private final IDeleteCategoryUc iDeleteCategoryUc;



    @Override
    public List<CategoryEntity> searchCategories(String ten) {
        return searchCategoryUc.searchCategories(ten);
    }



    @Override
    public CategoryEntity createCategory(CreateCategoryReq category) {
        return createCategoryService.process(category);
    }

    @Override
    public List<CategoryEntity> getAllCategories() {
        return getAllCategoryService.getAllCategories();
    }

    @Override
    public CategoryEntity updateCategory(String categoryId, UpdateCategoryReq req) {

        return updateCategoryService.process( categoryId,req);
    }

    @Override
    public void deleteCategory(String categoryId){
        iDeleteCategoryUc.process(categoryId);
    }


}
