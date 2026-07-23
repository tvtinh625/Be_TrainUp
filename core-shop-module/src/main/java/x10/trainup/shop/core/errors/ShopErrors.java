package x10.trainup.shop.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum ShopErrors implements ErrorDescriptor {

    SHOP_NAME_ALREADY_EXISTS(409, "SHOP.NAME_EXISTS", "Shop name already exists"),
    SHOP_NOT_FOUND(404, "SHOP.NOT_FOUND", "Shop not found"),
    INVALID_SHOP_DATA(400, "SHOP.INVALID_DATA", "Invalid shop data"),
    INVALID_ADDRESS_DATA(400, "SHOP.INVALID_ADDRESS", "Invalid address data"),
    SHOP_CREATION_FAILED(500, "SHOP.CREATION_FAILED", "Failed to create shop");

    private final int http;
    private final String code;
    private final String msg;

    ShopErrors(int http, String code, String msg) {
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