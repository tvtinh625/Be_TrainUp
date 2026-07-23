package x10.trainup.cart.core.error;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum CartError implements ErrorDescriptor {

    INVALID_PASSWORD(401, "USR.INVALID_PASSWORD", "Invalid password"),

    // Cart errors
    CART_NOT_FOUND(404, "CART.NOT_FOUND", "Không tìm thấy giỏ hàng của người dùng"),
    CART_IS_EMPTY(400, "CART.IS_EMPTY", "Giỏ hàng trống"),

    // Item errors
    ITEM_NOT_FOUND(404, "ITEM.NOT_FOUND", "Item không tồn tại trong giỏ hàng"),

    // Product errors
    PRODUCT_NOT_FOUND(404, "CART.PRODUCT_NOT_FOUND", "Sản phẩm không tồn tại"),
    SIZE_NOT_FOUND(404, "CART.SIZE_NOT_FOUND", "Size không tồn tại"),
    FLAVOR_NOT_FOUND(404, "CART.FLAVOR_NOT_FOUND", "Không tìm thấy Flavor"),
    INSUFFICIENT_STOCK(400, "CART.INSUFFICIENT_STOCK", "Không đủ hàng trong kho"),
    PRODUCT_INACTIVE(400, "CART.PRODUCT_INACTIVE", "Sản phẩm đã ngưng kinh doanh"),

    // User errors
    USER_NOT_FOUND(404, "CART.USER_NOT_FOUND", "Không tìm thấy người dùng");

    private final int http;
    private final String code;
    private final String msg;

    CartError(int http, String code, String msg) {
        this.http = http;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int httpStatus() {
        return http;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return msg;
    }
}