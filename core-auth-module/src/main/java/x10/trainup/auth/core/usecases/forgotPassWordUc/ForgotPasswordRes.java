package x10.trainup.auth.core.usecases.forgotPassWordUc;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ForgotPasswordRes {
    private final String recoveryToken;
    private final String email;
}
