package x10.trainup.cart.core.usecases.inCreaseCartUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.error.CartError;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.product.core.repositories.IRepositoryProduct;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@AllArgsConstructor
public class CreaseCartUcImpl implements IInCreaseCartUC {

    private final ICoreUserSerivce iCoreUserSerivce;
    private final IRepositoryCart iRepositoryCart;
    private final IRepositoryProduct iRepositoryProduct;

    @Override
    public void increase(IncreaseCartReq req, String userId) {

        // 1️⃣ Kiểm tra user tồn tại
        if (!iCoreUserSerivce.existsById(userId)) {
            throw new BusinessException(UserError.USER_NOT_FOUND, "Không tìm thấy người dùng.");
        }

        // 2️⃣ Tìm giỏ hàng hiện có
        CartEntity cart = iRepositoryCart.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CartError.CART_NOT_FOUND, "Không tìm thấy giỏ hàng của người dùng."));

        // 3️⃣ Kiểm tra sản phẩm có tồn tại
        var productOpt = iRepositoryProduct.findById(req.getProductId());
        if (!productOpt.isPresent()) {
            throw new BusinessException(CartError.PRODUCT_NOT_FOUND,
                    "ProductId: " + req.getProductId() + " không tồn tại.");
        }
        var product = productOpt.get();

        // 4️⃣ Kiểm tra size / flavor hợp lệ
        if (req.getSizeId() != null && !iRepositoryProduct.existsSize(req.getProductId(), req.getSizeId())) {
            throw new BusinessException(CartError.SIZE_NOT_FOUND,
                    "SizeId: " + req.getSizeId() + " không tồn tại.");
        }

        if (req.getFlavorId() != null &&
                !iRepositoryProduct.existsFlavor(req.getProductId(), req.getSizeId(), req.getFlavorId())) {
            throw new BusinessException(CartError.FLAVOR_NOT_FOUND,
                    "FlavorId: " + req.getFlavorId() + " không tồn tại.");
        }

        // 5️⃣ Tìm item trong giỏ để tăng số lượng
        boolean itemFound = false;
        for (CartItemEntity item : cart.getItems()) {
            if (item.getProductId().equals(req.getProductId())
                    && ((item.getSizeId() == null && req.getSizeId() == null) ||
                    (item.getSizeId() != null && item.getSizeId().equals(req.getSizeId())))
                    && ((item.getFlavorId() == null && req.getFlavorId() == null) ||
                    (item.getFlavorId() != null && item.getFlavorId().equals(req.getFlavorId())))
            ) {
                // 🔹 Kiểm tra số lượng tồn kho trước khi tăng
                if (req.getFlavorId() != null && req.getSizeId() != null) {
                    // Lấy thông tin flavor từ product
                    var sizeOpt = product.getSizes().stream()
                            .filter(s -> s.getId().equals(req.getSizeId()))
                            .findFirst();

                    if (sizeOpt.isPresent()) {
                        var flavorOpt = sizeOpt.get().getFlavors().stream()
                                .filter(f -> f.getId().equals(req.getFlavorId()))
                                .findFirst();

                        if (flavorOpt.isPresent()) {
                            var flavor = flavorOpt.get();

                            // Kiểm tra flavor có đang active không
                            if (!flavor.isActive()) {
                                throw new BusinessException(CartError.PRODUCT_INACTIVE,
                                        "Sản phẩm '" + product.getName() + "' với hương vị này đã ngưng kinh doanh.");
                            }

                            // Kiểm tra số lượng sau khi tăng có vượt quá tồn kho không
                            int quantityInStock = flavor.getQuantityInStock();
                            int newQuantity = item.getQuantity() + 1;

                            if (newQuantity > quantityInStock) {
                                throw new BusinessException(CartError.INSUFFICIENT_STOCK,
                                        "Sản phẩm '" + product.getName() + "' chỉ còn " +
                                                quantityInStock + " sản phẩm trong kho. " +
                                                "Bạn đang có " + item.getQuantity() + " sản phẩm trong giỏ.");
                            }
                        }
                    }
                }

                // Tăng số lượng
                item.setQuantity(item.getQuantity() + 1);
                itemFound = true;
                break;
            }
        }

        // 6️⃣ Nếu không tìm thấy item → báo lỗi
        if (!itemFound) {
            throw new BusinessException(CartError.ITEM_NOT_FOUND,
                    "Không tìm thấy sản phẩm trong giỏ hàng.");
        }

        // 7️⃣ Cập nhật tổng giá trị
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);

        // 8️⃣ Cập nhật thời gian
        cart.setUpdatedAt(Instant.now());

        // 9️⃣ Lưu thay đổi
        iRepositoryCart.save(cart);
    }
}