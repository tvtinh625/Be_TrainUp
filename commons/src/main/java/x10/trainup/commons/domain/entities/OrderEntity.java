package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.OrderStatus;
import x10.trainup.commons.domain.enums.PaymentMethod;
import x10.trainup.commons.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    private String id;                          // ID đơn hàng
    private String orderNumber;                 // Mã đơn hàng (ORD-20250115-001)
    private String userId;                      // ID người đặt hàng
    private OrderStatus status;                 // PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
    private PaymentStatus paymentStatus;        // UNPAID, PAID
    private PaymentMethod paymentMethod;        // COD, BANK_TRANSFER, MOMO
    private List<OrderItemEntity> items;        // Danh sách sản phẩm
    private BigDecimal subtotal;                // Tổng tiền hàng
    private BigDecimal shippingFee;             // Phí ship
    
    // Khuyến mãi & Điểm thưởng
    private String couponCode;                  // Mã giảm giá
    private BigDecimal couponDiscount;          // Số tiền giảm từ Coupon
    private Long pointsRedeemed;                // Số điểm đã dùng
    private BigDecimal pointsDiscount;          // Số tiền giảm từ Điểm
    private BigDecimal tierDiscount;            // Số tiền giảm từ Hạng thành viên (Silver/Gold/Diamond)
    private Long pointsEarned;                  // Số điểm tích lũy từ đơn hàng này
    
    private BigDecimal totalAmount;             // Tổng tiền = subtotal + shippingFee - couponDiscount - pointsDiscount - tierDiscount
    private ShippingAddressEntity shippingAddress;            // Địa chỉ giao hàng (text đầy đủ)
    private String note;                        // Ghi chú từ khách
    private Instant createdAt;                  // Thời gian tạo đơn
    private Instant updatedAt;                  // Thời gian cập nhật
}