package x10.trainup.auth.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum AuthError implements ErrorDescriptor {
    INVALID_REFRESH_TOKEN(401, "AUTH.INVALID_REFRESH_TOKEN", "Refresh token không hợp lệ"),
    REFRESH_TOKEN_EXPIRED(401, "AUTH.REFRESH_TOKEN_EXPIRED", "Refresh token đã hết hạn"),
    ACCESS_DENIED(403, "AUTH.ACCESS_DENIED", "Không có quyền truy cập"),
    UNAUTHORIZED(401, "AUTH.UNAUTHORIZED", "Chưa đăng nhập hoặc token không hợp lệ");

    private final int http;
    private final String code;
    private final String msg;

    AuthError(int http, String code, String msg) {
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
