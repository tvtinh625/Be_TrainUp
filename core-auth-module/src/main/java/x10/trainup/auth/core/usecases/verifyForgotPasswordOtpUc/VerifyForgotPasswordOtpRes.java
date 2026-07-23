package x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyForgotPasswordOtpRes {
    private String resetToken;
}
