package x10.trainup.user.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum UserError implements ErrorDescriptor {

    // 🔐 Auth & Password
    INVALID_PASSWORD(401, "USR.INVALID_PASSWORD", "Invalid password"),
    INVALID_OLD_PASSWORD(400, "USR.INVALID_OLD_PASSWORD", "Old password is incorrect"),
    SAME_AS_OLD_PASSWORD(400, "USR.SAME_AS_OLD_PASSWORD", "New password cannot be the same as old password"),

    // 📧 Email
    EMAIL_EXISTS(409, "USR.EMAIL_EXISTS", "Email already exists"),
    INVALID_EMAIL(400, "USR.INVALID_EMAIL", "Invalid email"),
    EMAIL_NOT_FOUND(404, "USR.EMAIL_NOT_FOUND", "Email not found"),
    EMAIL_NOT_VERIFIED(403, "USR.EMAIL_NOT_VERIFIED", "Email not verified"),
    EMAIL_ALREADY_VERIFIED(400, "USR.EMAIL_ALREADY_VERIFIED", "Email already verified"),
    EMAIL_SEND_FAILED(500, "USR.EMAIL_SEND_FAILED", "Failed to send email"),

    // 👤 User Account
    USER_NOT_FOUND(404, "USR.NOT_FOUND", "User not found"),
    ACCOUNT_NOT_VERIFIED(403, "USR.ACCOUNT_NOT_VERIFIED", "Account not verified"),
    ACCOUNT_DISABLED(403, "USR.ACCOUNT_DISABLED", "Account is disabled"),
    USER_INACTIVE(403, "USR.USER_INACTIVE", "User is inactive or not allowed to perform this action"),

    // 🔑 OTP & Reset
    // 🔑 OTP & Reset
    OTP_REQUEST_TOO_SOON(429, "USR.OTP_REQUEST_TOO_SOON", "OTP requested too frequently"),
    OTP_INVALID(400, "USR.OTP_INVALID", "Invalid OTP"),
    OTP_EXPIRED(400, "USR.OTP_EXPIRED", "OTP has expired"),
    TOKEN_INVALID(400, "USR.TOKEN_INVALID", "Invalid or expired token"), // ✅ thêm dòng này
    RESET_TOKEN_INVALID(400, "USR.RESET_TOKEN_INVALID", "Invalid or expired reset token"),
    PASSWORD_RESET_FAILED(500, "USR.PASSWORD_RESET_FAILED", "Failed to reset password"),

    // 🖼️ Profile Update
    UPLOAD_FAILED(500, "USR.UPLOAD_FAILED", "Failed to upload user media"),
    USERNAME_EXISTS(409, "USR.USERNAME_EXISTS", "Username already exists"),

    INVALID_STATUS_TRANSITION(400, "USR.INVALID_STATUS_TRANSITION", "Invalid status transition"),
    SAME_STATUS(400, "USR.SAME_STATUS", "User is already in this status"),
    STATUS_UPDATE_FAILED(500, "USR.STATUS_UPDATE_FAILED", "Failed to update user status");


    private final int http;
    private final String code;
    private final String msg;

    UserError(int http, String code, String msg) {
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
