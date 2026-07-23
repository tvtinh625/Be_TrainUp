package x10.trainup.order.core.usecases.createOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddressReq {
    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;

    @NotBlank(message =  " Email không được để trống  ")
    private String email ;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(0|\\+84)(\\d{9,10})$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phoneNumber;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;


    @NotNull(message = "Province ID is required")
    private Integer provinceId;     // Mã tỉnh (GHN)

    @NotNull(message = "District ID is required")
    private Integer districtId;     // Mã quận/huyện (GHN)

    @NotNull(message = "Integer ID is required")
    private Integer wardCode;



}
