package x10.trainup.auth.core.usecases;

import x10.trainup.auth.core.usecases.ResetPasswordUc.ResetPasswordReq;
import x10.trainup.auth.core.usecases.firebaseIdentity.FirebaseIdentityUcResp;
import x10.trainup.auth.core.usecases.firebaseIdentity.FirebaseSignUpUcRequest;
import x10.trainup.auth.core.usecases.forgotPassWordUc.ForgotPasswordReq;
import x10.trainup.auth.core.usecases.forgotPassWordUc.ForgotPasswordRes;
import x10.trainup.auth.core.usecases.refreshToken.RefreshTokenRequest;
import x10.trainup.auth.core.usecases.refreshToken.RefreshTokenResponse;
import x10.trainup.auth.core.usecases.signInUc.SignInReq;
import x10.trainup.auth.core.usecases.signInUc.SignInResp;
import x10.trainup.auth.core.usecases.signUpUc.SignUpReq;
import x10.trainup.auth.core.usecases.signUpUc.SignUpResp;
import x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc.VerifyForgotPasswordOtpReq;
import x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc.VerifyForgotPasswordOtpRes;

public interface ICoreAuthService {
    SignUpResp SignUp(SignUpReq req);
    SignInResp SignIn(SignInReq req);
    void resendVerificationEmail(String email);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    FirebaseIdentityUcResp identityWithFirebase(FirebaseSignUpUcRequest req);
    ForgotPasswordRes forgotPassword(ForgotPasswordReq req);
    void resetPassword(String resetToken,ResetPasswordReq req);

    VerifyForgotPasswordOtpRes verifyForgotPasswordOtp(String recoveryToken, VerifyForgotPasswordOtpReq req);

}
