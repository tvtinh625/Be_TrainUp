package x10.trainup.commons.domain.enums;

public enum EmailVerifyStatus {
    SUCCESS,            // ✅ Xác minh thành công
    EXPIRED,            // ⚠️ Token hết hạn
    ALREADY_VERIFIED,   // 🔁 Email đã xác minh trước đó
    INVALID             // ❌ Token không hợp lệ
}
