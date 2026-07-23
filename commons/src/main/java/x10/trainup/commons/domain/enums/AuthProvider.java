package x10.trainup.commons.domain.enums;

public enum AuthProvider {
    LOCAL,    // Đăng ký bằng email & password
    GOOGLE,   // Đăng nhập bằng Google
    FACEBOOK,  // (tuỳ chọn nếu mở rộng)
    GUEST  // ← Thêm provider cho guest use
}
