package x10.trainup.auth.core.usecases.forgotPassWordUc;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class ForgotPasswordReq {
    @NotBlank
    @Email
    private String email;
}