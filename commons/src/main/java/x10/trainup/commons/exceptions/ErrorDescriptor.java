package x10.trainup.commons.exceptions;

/**
 * Chuẩn chung mô tả lỗi.
 * Mọi enum/domain-specific error đều implement interface này.
 */
public interface ErrorDescriptor {
    int httpStatus();          // HTTP status: 400 / 404 / 409 / ...
    String code();             // Mã lỗi ổn định, có namespace: USER.EMAIL_EXISTS
    String defaultMessage();   // Thông điệp mặc định (có thể override qua i18n)
}
