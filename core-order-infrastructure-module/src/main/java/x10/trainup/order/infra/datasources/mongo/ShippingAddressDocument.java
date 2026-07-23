package x10.trainup.order.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressDocument {

    private String recipientName;    // Tên người nhận
    private String email;            // Email để gửi thông báo
    private String phoneNumber;      // SĐT người nhận

    private String address;          // Số nhà + đường
    private String ward;             // Tên phường/xã
    private String district;         // Tên quận/huyện
    private String city;             // Tên tỉnh/thành phố
    private String country;          // Quốc gia

    private int provinceId;          // Mã tỉnh
    private int districtId;          // Mã quận/huyện
    private int wardCode;         // Mã phường/xã
    private String fullAddress;      // Địa chỉ đầy đủ
}
