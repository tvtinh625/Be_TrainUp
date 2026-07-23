package x10.trainup.address.core.dto;

import lombok.Data;

@Data
public class ShippingFeeRequest {
    private int fromDistrictId;      // Quận/Huyện gửi hàng
    private String fromWardCode;     // Phường/Xã gửi hàng
    private int toDistrictId;        // Quận/Huyện nhận hàng
    private String toWardCode;       // Phường/Xã nhận hàng
    private int weight;              // Khối lượng (gram)
    private Integer insuranceValue;  // Giá trị hàng hóa (để tính bảo hiểm)
    private Integer serviceTypeId;   // ID dịch vụ (2 = Express, 5 = Tiết kiệm)
}