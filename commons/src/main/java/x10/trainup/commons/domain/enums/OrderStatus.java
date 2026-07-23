package x10.trainup.commons.domain.enums;

public enum OrderStatus {
    PENDING,            // Chờ xác nhận
    CONFIRMED,          // Đã xác nhận
    SHIPPING,           // Đang giao
    DELIVERED,          // Giao thành công
    CANCELLED           // Đã hủy
}