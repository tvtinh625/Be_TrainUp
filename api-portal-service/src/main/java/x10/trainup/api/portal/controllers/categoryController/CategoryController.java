package x10.trainup.api.portal.controllers.categoryController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.category.core.usecases.ICoreCategoryService;
import x10.trainup.category.core.usecases.createCategoryUc.CreateCategoryReq;
import x10.trainup.category.core.usecases.updateCategoryUc.UpdateCategoryReq;
import x10.trainup.commons.domain.entities.CategoryEntity;
import x10.trainup.commons.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICoreCategoryService iCoreCategoryService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryEntity>>> getAllCategories(
            HttpServletRequest request
    ) {
        List<CategoryEntity> categories = iCoreCategoryService.getAllCategories();
        ApiResponse<List<CategoryEntity>> response = ApiResponse.of(
                200,
                "CATEGORY.LIST_RETRIEVED",
                "Lấy danh sách danh mục thành công",
                categories,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryEntity>>> searchCategories(
            @RequestParam("ten") String ten,
            HttpServletRequest request
    ) {
        List<CategoryEntity> result = iCoreCategoryService.searchCategories(ten.trim());

        ApiResponse<List<CategoryEntity>> response = ApiResponse.of(
                200,
                "CATEGORY.SEARCH_SUCCESS",
                "Tìm kiếm danh mục thành công",
                result,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }
}
