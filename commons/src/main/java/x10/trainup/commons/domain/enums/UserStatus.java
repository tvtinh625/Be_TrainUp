package x10.trainup.commons.domain.enums;



public enum UserStatus {
    PENDING,   // mới đăng ký, chưa xác thực email
    ACTIVE,    // đã xác thực email
    INACTIVE,  // bị vô hiệu hóa tạm thời
    BLOCKED;   // bị chặn vĩnh viễn
}
