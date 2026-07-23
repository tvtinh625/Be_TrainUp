package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddressEntity {

    private String recipientName;    // Tên người nhận
    private String email;            // Email để gửi thông báo
    private String phoneNumber;      // Số điện thoại

    private String address;          // Số nhà + tên đường
    private String ward;             // Tên phường/xã
    private String district;         // Tên quận/huyện
    private String city;             // Tên tỉnh/thành phố
    private String country;          // Quốc gia (default: Vietnam)

    private int provinceId;          // Mã tỉnh (GHN/GHTK)
    private int districtId;          // Mã quận/huyện
    private int wardCode;         // Mã phường/xã

    private String fullAddress;      // Địa chỉ đầy đủ (tự gen)

    // Tạo fullAddress tự động
    public String getFullAddress() {
        if (fullAddress == null || fullAddress.isBlank()) {
            fullAddress = String.format(
                    "%s, %s, %s, %s, %s",
                    address,
                    ward,
                    district,
                    city,
                    country != null ? country : "Việt Nam"
            );
        }
        return fullAddress;
    }
}
