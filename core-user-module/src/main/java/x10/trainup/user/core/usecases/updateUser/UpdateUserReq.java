package x10.trainup.user.core.usecases.updateUser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.entities.AddressEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserReq {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 50, message = "Họ và tên không được vượt quá 50 ký tự")
    @Pattern(
            regexp = "^[a-zA-ZÀ-ỹ\\s]+$",
            message = "Họ và tên chỉ được chứa chữ cái (có thể có dấu tiếng Việt) và khoảng trắng"
    )
    private String username;

    @Pattern(
            regexp = "^(|0[3|5|7|8|9][0-9]{8}|\\+84[3|5|7|8|9][0-9]{8})$",
            message = "Số điện thoại không hợp lệ (VD: 090xxxxxxx hoặc +8490xxxxxxx)"
    )
    private String phone;

    private String newAvatarKey;
    private String newCoverKey;
    private AddressEntity address; // địa chỉ
    private boolean removeAvatar;
    private boolean removeCover;
}