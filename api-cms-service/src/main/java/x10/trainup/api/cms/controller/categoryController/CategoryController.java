package x10.trainup.api.cms.controller.categoryController;

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

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryEntity>> createCategory(
            @Valid @RequestBody CreateCategoryReq req,
            HttpServletRequest request
    ) {
        req.normalize();

        if (!req.isValid()) {
            ApiResponse<CategoryEntity> error = ApiResponse.of(
                    400,
                    "CATEGORY.INVALID",
                    "Tên danh mục không hợp lệ",
                    null,
                    request.getRequestURI(),
                    null
            );
            return ResponseEntity.badRequest().body(error);
        }

        CategoryEntity created = iCoreCategoryService.createCategory(req);

        ApiResponse<CategoryEntity> response = ApiResponse.of(
                200,
                "CATEGORY.CREATED",
                "Tạo danh mục thành công",
                created,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // ✅ API: Lấy tất cả danh mục
    // -------------------------------
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

    // -------------------------------
    // ✅ API: Cập nhật danh mục
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryEntity>> updateCategory(
            @PathVariable("id") String categoryId,
            @Valid @RequestBody UpdateCategoryReq req,
            HttpServletRequest request
    ) {
        req.normalize();
        CategoryEntity updated = iCoreCategoryService.updateCategory(categoryId, req);

        ApiResponse<CategoryEntity> response = ApiResponse.of(
                200,
                "CATEGORY.UPDATED",
                "Cập nhật danh mục thành công",
                updated,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }

    // -------------------------------
    // ✅ API: Search danh mục theo tên (fuzzy search)
    // -------------------------------
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable("id") String categoryId,
            HttpServletRequest request
    ) {
        iCoreCategoryService.deleteCategory(categoryId);

        ApiResponse<Void> response = ApiResponse.of(
                200,
                "CATEGORY.DELETED",
                "Xóa danh mục thành công",
                null,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }
}
