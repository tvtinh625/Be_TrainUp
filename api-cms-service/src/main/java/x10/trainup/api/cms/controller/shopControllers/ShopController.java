package x10.trainup.api.cms.controller.shopControllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.ShopEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.shop.core.usecases.ICoreShopServiceUc;
import x10.trainup.shop.core.usecases.createShop.CreateShopReq;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@AllArgsConstructor
public class ShopController {


    private final ICoreShopServiceUc shopService;
    @PostMapping
    public ResponseEntity<ApiResponse<ShopEntity>> create(
            @Valid @RequestBody CreateShopReq req,
            HttpServletRequest request
    ) {
        ShopEntity createdShop = shopService.create(req);

        ApiResponse<ShopEntity> response = ApiResponse.of(
                HttpStatus.CREATED.value(),
                "SHOP.CREATED",
                "Shop created successfully",
                createdShop,
                request.getRequestURI(),
                request.getHeader("X-Trace-Id")
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopEntity>>> getAll(
            HttpServletRequest request
    ) {
        List<ShopEntity> shops = shopService.getAll();

        ApiResponse<List<ShopEntity>> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "SHOP.LIST",
                "Get all shops successfully",
                shops,
                request.getRequestURI(),
                request.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }


}
