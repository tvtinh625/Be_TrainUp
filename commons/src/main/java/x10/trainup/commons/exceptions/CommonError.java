package x10.trainup.commons.exceptions;

/**
 * Các lỗi chung hệ thống, không thuộc domain cụ thể.
 */
public enum CommonError implements ErrorDescriptor {
    REQUEST_INVALID(400, "COMMON.REQUEST_INVALID", "Request is invalid"),
    UNAUTHORIZED(401, "COMMON.UNAUTHORIZED", "Unauthorized"),
    FORBIDDEN(403, "COMMON.FORBIDDEN", "Forbidden"),
    NOT_FOUND(404, "COMMON.NOT_FOUND", "Resource not found"),
    CONFLICT(409, "COMMON.CONFLICT", "Conflict occurred"),
    TOO_MANY_REQUESTS(429, "COMMON.TOO_MANY_REQUESTS", "Too many requests"),
    INTERNAL_ERROR(500, "COMMON.INTERNAL_ERROR", "Unexpected server error");

    private final int httpStatus;
    private final String code;
    private final String defaultMessage;

    CommonError(int httpStatus, String code, String defaultMessage) {
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
