package x10.trainup.api.portal.controllers.productController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.product.core.usecases.ICoreProductService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ICoreProductService iCoreProductService;

    @GetMapping("/all")
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

    // 🟢 Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductEntity>> getProductById(
            @PathVariable String id,
            HttpServletRequest request
    ) {
        Optional<ProductEntity> productOpt = iCoreProductService.findProductById(id);

        if (productOpt.isEmpty()) {
            ApiResponse<ProductEntity> notFoundResponse = ApiResponse.of(
                    HttpStatus.NOT_FOUND.value(),
                    "PRODUCT.NOT_FOUND",
                    "Không tìm thấy sản phẩm với ID: " + id,
                    null,
                    request.getRequestURI(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
        }

        ApiResponse<ProductEntity> successResponse = ApiResponse.of(
                HttpStatus.OK.value(),
                "PRODUCT.RETRIEVED",
                "Lấy sản phẩm thành công",
                productOpt.get(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.ok(successResponse);
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
