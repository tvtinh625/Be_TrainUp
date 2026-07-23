package x10.trainup.category.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum CategoryError implements ErrorDescriptor {

    CATEGORY_NAME_REQUIRED(400, "CATEGORY.NAME_REQUIRED", "Tên danh mục không được để trống"),
    CATEGORY_ALREADY_EXISTS(409, "CATEGORY.ALREADY_EXISTS", "Danh mục này đã tồn tại"),
    CATEGORY_NOT_FOUND(404, "CATEGORY.NOT_FOUND", "Không tìm thấy danh mục"),
    CATEGORY_IN_USE(409, "CATEGORY.IN_USE", "Không thể xoá vì danh mục đang được sử dụng bởi sản phẩm khác");

    private final int httpStatus;
    private final String code;
    private final String defaultMessage;

    CategoryError(int httpStatus, String code, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public int httpStatus() {
        return httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
