package x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc;


import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyForgotPasswordOtpReq {
    @Pattern(regexp = "\\d{6}")
    private String otp;
}