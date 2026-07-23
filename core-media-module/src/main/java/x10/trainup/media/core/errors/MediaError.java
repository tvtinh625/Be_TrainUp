package x10.trainup.media.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

/**
 * Chuẩn hoá mã lỗi cho module media.
 * Dùng chung cho upload, delete, presign,... trong hệ thống.
 */
public enum MediaError implements ErrorDescriptor {

    // ====== Upload ======
    UPLOAD_FAILED(500, "MEDIA.UPLOAD_FAILED", "Tải tệp lên thất bại."),
    INVALID_FILE_TYPE(400, "MEDIA.INVALID_FILE_TYPE", "Loại tệp không hợp lệ."),
    EMPTY_FILE(400, "MEDIA.EMPTY_FILE", "Tệp rỗng hoặc không tồn tại."),
    FILE_TOO_LARGE(413, "MEDIA.FILE_TOO_LARGE", "Dung lượng tệp vượt quá giới hạn cho phép."),

    // ====== Delete ======
    DELETE_FAILED(500, "MEDIA.DELETE_FAILED", "Xoá tệp thất bại."),
    MEDIA_NOT_FOUND(404, "MEDIA.NOT_FOUND", "Không tìm thấy tệp media."),

    // ====== Presign ======
    PRESIGN_FAILED(500, "MEDIA.PRESIGN_FAILED", "Không thể tạo URL tải lên."),
    INVALID_MEDIA_TYPE(400, "MEDIA.INVALID_MEDIA_TYPE", "Media type không hợp lệ."),

    // ====== Cấu hình / hệ thống ======
    CONFIG_ERROR(500, "MEDIA.CONFIG_ERROR", "Lỗi cấu hình hệ thống media."),
    NETWORK_ERROR(504, "MEDIA.NETWORK_ERROR", "Kết nối tới dịch vụ lưu trữ bị gián đoạn."),
    UNSUPPORTED_OPERATION(400, "MEDIA.UNSUPPORTED_OPERATION", "Thao tác media không được hỗ trợ.");

    private final int http;
    private final String code;
    private final String msg;

    MediaError(int http, String code, String msg) {
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
