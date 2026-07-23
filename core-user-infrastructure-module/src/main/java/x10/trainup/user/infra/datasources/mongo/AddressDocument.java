package x10.trainup.user.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDocument {
    private String street;   // Tên đường (VD: "Nguyễn Trãi")
    private String ward;     // Xã/Phường (VD: "Phường 5")
    private String district; // Quận/Huyện (VD: "Quận 5")
    private String province; // Tỉnh/Thành phố (VD: "TP.HCM")
    private String country;  // Quốc gia (VD: "Việt Nam")
    private int provinceId;    // Mã tỉnh
    private int districtId;    // Mã quận/huyện
    private int wardCode;   // Mã phường/xã
}
