package x10.trainup.api.cms.controller.productController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.product.core.usecases.ICoreProductService;
import x10.trainup.product.core.usecases.createProductUc.CreateProductReq;
import x10.trainup.product.core.usecases.updateProductUc.UpdateProductReq;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ICoreProductService iCoreProductService;


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable String id,
            HttpServletRequest request) {

        iCoreProductService.deleteProduct(id);

        ApiResponse<Void> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "PRODUCT.DELETED",
                "Xóa sản phẩm thành công",
                null,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductEntity>>> getAllProducts(
            HttpServletRequest request) {

        List<ProductEntity> products = iCoreProductService.getAllProducts();

        ApiResponse<List<ProductEntity>> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "PRODUCTS.RETRIEVED",
                "Lấy danh sách sản phẩm thành công",
                products,
                request.getRequestURI(),
                null // traceId có thể lấy từ MDC hoặc log context nếu có
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductEntity>> createProduct(
            @RequestBody  @Valid CreateProductReq req,
            HttpServletRequest request) {

        ProductEntity createdProduct = iCoreProductService.createProduct(req);

        ApiResponse<ProductEntity> response = ApiResponse.of(
                HttpStatus.CREATED.value(),
                "PRODUCT.CREATED",
                "Tạo sản phẩm thành công",
                createdProduct,
                request.getRequestURI(),
                null // traceId có thể lấy từ MDC hoặc log context nếu có
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductEntity>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductReq req,
            HttpServletRequest request) {

        ProductEntity updatedProduct = iCoreProductService.updateProduct(id, req);

        ApiResponse<ProductEntity> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "PRODUCT.UPDATED",
                "Cập nhật sản phẩm thành công",
                updatedProduct,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductEntity>>> getProductsByCategoryId(
            @PathVariable String categoryId,
            HttpServletRequest request
    ) {
        List<ProductEntity> products = iCoreProductService.findProductsByCategoryId(categoryId);

        if (products.isEmpty()) {
            ApiResponse<List<ProductEntity>> notFoundResponse = ApiResponse.of(
                    HttpStatus.NOT_FOUND.value(),
                    "PRODUCTS.NOT_FOUND",
                    "Không có sản phẩm nào thuộc danh mục với ID: " + categoryId,
                    null,
                    request.getRequestURI(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
        }

        ApiResponse<List<ProductEntity>> successResponse = ApiResponse.of(
                HttpStatus.OK.value(),
                "PRODUCTS.RETRIEVED_BY_CATEGORY",
                "Lấy danh sách sản phẩm theo categoryId thành công",
                products,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductEntity>>> searchProductsByName(
            @RequestParam("name") String name,
            HttpServletRequest request
    ) {
        List<ProductEntity> products = iCoreProductService.searchProductsByName(name);

        if (products.isEmpty()) {
            ApiResponse<List<ProductEntity>> notFoundResponse = ApiResponse.of(
                    HttpStatus.NOT_FOUND.value(),
                    "PRODUCTS.NOT_FOUND",
                    "Không tìm thấy sản phẩm nào có tên chứa: " + name,
                    null,
                    request.getRequestURI(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
        }

        ApiResponse<List<ProductEntity>> successResponse = ApiResponse.of(
                HttpStatus.OK.value(),
                "PRODUCTS.SEARCH_SUCCESS",
                "Tìm kiếm sản phẩm thành công",
                products,
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(successResponse);
    }
}
