package x10.trainup.user.core.usecases.createGuestUserUc;

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
public class CreateGuestUserReq {

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(0|\\+84)(\\d{9})$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "Street address is required")
    private String street;

    @NotBlank(message = "Ward is required")
    private String ward;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Province is required")
    private String province;

    private String country; // default: Vietnam

    // =============================
    // → THÊM 3 TRƯỜNG MÃ ĐỊA CHÍNH
    // =============================

    @NotNull(message = "Province ID is required")
    private Integer provinceId;     // Mã tỉnh (GHN)

    @NotNull(message = "District ID is required")
    private Integer districtId;     // Mã quận/huyện (GHN)

    @NotNull(message = "Integer ID is required")
    private Integer wardCode;        // Mã phường (GHN)
}
