package x10.trainup.user.core.usecases.createUserWithoutFirebaseUc;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.UserStatus;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CreateUserFireBaseReq {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 64, message = "Username must be at least 3 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be at least 8 characters")
    private String password; // plaintext từ client

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9+(). -]{7,20}$", message = "Invalid phone format")
    private String phone;

    private String avatarUrl;

    private String providerId;

}
