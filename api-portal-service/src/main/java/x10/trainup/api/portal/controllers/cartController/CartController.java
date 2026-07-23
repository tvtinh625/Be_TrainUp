package x10.trainup.api.portal.controllers.cartController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import x10.trainup.cart.core.usecases.ICoreCartService;
import x10.trainup.cart.core.usecases.RemoveListItemsUC.RemoveListItemsReq;
import x10.trainup.cart.core.usecases.createCartUc.CreateCartReq;
import x10.trainup.cart.core.usecases.createCartUc.CreateCartRes;
import x10.trainup.cart.core.usecases.deCreaseCartUc.DecreaseReq;
import x10.trainup.cart.core.usecases.getCartUc.GetCartRes;
import x10.trainup.cart.core.usecases.inCreaseCartUc.IncreaseCartReq;
import x10.trainup.cart.core.usecases.removeItemUc.RemoveItemReq;
import x10.trainup.cart.core.usecases.updateCartItemUc.UpdateQuantityReq;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.security.core.principal.UserPrincipal;

@RestController
@RequestMapping("api/carts")
@AllArgsConstructor
public class CartController {
    private final ICoreCartService iCoreCartService;

    @PutMapping("/update-quantity")
    public ResponseEntity<ApiResponse<String>> updateQuantity(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid UpdateQuantityReq req,
            HttpServletRequest httpRequest
    ) {
        iCoreCartService.updateCartItemQuantity(req, user.getId());

        ApiResponse<String> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Cập nhật số lượng sản phẩm thành công",
                null,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-item")
    public ResponseEntity<ApiResponse<String>> removeItem(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid RemoveItemReq req,
            HttpServletRequest httpRequest
    ) {
        iCoreCartService.removeItemFromCart(req, user.getId());

        ApiResponse<String> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Xóa sản phẩm khỏi giỏ hàng thành công",
                null,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest
    ) {
        iCoreCartService.clearCart( user.getId());
        ApiResponse<String> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Xóa toàn bộ giỏ hàng thành công",
                null,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateCartRes>> addToCart(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid CreateCartReq req,
            HttpServletRequest httpRequest
    ) {
        CreateCartRes res = iCoreCartService.createCart(req, user.getId());

        ApiResponse<CreateCartRes> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Thêm sản phẩm vào giỏ hàng thành công",
                res,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/increase")
    public ResponseEntity<ApiResponse<String>> increaseCart(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid IncreaseCartReq req,
            HttpServletRequest httpRequest
    ) {
        iCoreCartService.inCreaseCart(req, user.getId());

        ApiResponse<String> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Tăng số lượng sản phẩm trong giỏ hàng thành công",
                null,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/decrease")
    public ResponseEntity<ApiResponse<String>> decrease(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid DecreaseReq req,
            HttpServletRequest httpRequest
    ) {
        iCoreCartService.deCreaseCart(req, user.getId());

        ApiResponse<String> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Giảm số lượng sản phẩm trong giỏ hàng thành công",
                null,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetCartRes>> getCart(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest httpRequest
    ) {


        GetCartRes res = iCoreCartService.getCart( user.getId());

        ApiResponse<GetCartRes> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Lấy thông tin giỏ hàng thành công",
                res,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/remove-list")
    public ResponseEntity<ApiResponse<String>> removeListItems(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid RemoveListItemsReq req,
            HttpServletRequest httpRequest
    ) {
        iCoreCartService.removeListItems(req, user.getId());

        ApiResponse<String> response = ApiResponse.of(
                200,
                "SUCCESS",
                "Xóa danh sách sản phẩm khỏi giỏ hàng thành công",
                null,
                httpRequest.getRequestURI(),
                httpRequest.getHeader("X-Trace-Id")
        );

        return ResponseEntity.ok(response);
    }
}