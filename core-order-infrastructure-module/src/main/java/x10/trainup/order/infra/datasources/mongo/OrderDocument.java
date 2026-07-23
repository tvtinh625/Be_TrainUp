package x10.trainup.order.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
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
@Document(collection = "orders")
public class OrderDocument {

    @Id
    private String id;
    private String orderNumber;            // Mã đơn hàng (ORD-20250115-001)
    private String userId;                 // ID người dùng đặt hàng
    private OrderStatus status;            // Trạng thái đơn hàng
    private PaymentStatus paymentStatus;   // UNPAID, PAID
    private PaymentMethod paymentMethod;   // COD, BANK_TRANSFER, MOMO
    private List<OrderItemDocument> items; // Danh sách sản phẩm
    private BigDecimal subtotal;           // Tổng tiền hàng
    private BigDecimal shippingFee;        // Phí ship
    
    // Khuyến mãi & Điểm thưởng
    private String couponCode;             // Mã giảm giá
    private BigDecimal couponDiscount;     // Số tiền giảm từ Coupon
    private Long pointsRedeemed;           // Số điểm đã dùng
    private BigDecimal pointsDiscount;     // Số tiền giảm từ Điểm
    private BigDecimal tierDiscount;       // Số tiền giảm từ Hạng thành viên
    private Long pointsEarned;             // Điểm tích lũy từ đơn hàng này
    
    private BigDecimal totalAmount;        // Tổng tiền = subtotal + shippingFee - giảm giá
    private ShippingAddressDocument  shippingAddress;        // Địa chỉ giao hàng (text đầy đủ)
    private String note;                   // Ghi chú từ khách

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}