package x10.trainup.auth.core.usecases;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.auth.core.usecases.ResetPasswordUc.IResetPasswordUc;
import x10.trainup.auth.core.usecases.ResetPasswordUc.ResetPasswordReq;
import x10.trainup.auth.core.usecases.firebaseIdentity.FirebaseIdentityUcResp;
import x10.trainup.auth.core.usecases.firebaseIdentity.FirebaseSignUpUcRequest;
import x10.trainup.auth.core.usecases.firebaseIdentity.IFirebaseIdentityUc;
import x10.trainup.auth.core.usecases.forgotPassWordUc.ForgotPasswordReq;
import x10.trainup.auth.core.usecases.forgotPassWordUc.ForgotPasswordRes;
import x10.trainup.auth.core.usecases.forgotPassWordUc.IForgotPasswordUc;
import x10.trainup.auth.core.usecases.refreshToken.IRefreshTokenUc;
import x10.trainup.auth.core.usecases.refreshToken.RefreshTokenRequest;
import x10.trainup.auth.core.usecases.refreshToken.RefreshTokenResponse;
import x10.trainup.auth.core.usecases.resendVerificationEmailUc.IResendVerificationEmailUc;
import x10.trainup.auth.core.usecases.signInUc.ISignInUC;
import x10.trainup.auth.core.usecases.signInUc.SignInReq;
import x10.trainup.auth.core.usecases.signInUc.SignInResp;
import x10.trainup.auth.core.usecases.signUpUc.ISignUpUc;
import x10.trainup.auth.core.usecases.signUpUc.SignUpReq;
import x10.trainup.auth.core.usecases.signUpUc.SignUpResp;
import x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc.IVerifyForgotPasswordOtpUc;
import x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc.VerifyForgotPasswordOtpReq;
import x10.trainup.auth.core.usecases.verifyForgotPasswordOtpUc.VerifyForgotPasswordOtpRes;

@Service
@AllArgsConstructor
public class CoreAuthSerivceImpl implements  ICoreAuthService{

    private final IRefreshTokenUc refreshTokenUc;
    private final ISignUpUc signUpUC;
    private final ISignInUC signInUC;
    private  final IResendVerificationEmailUc resendVerificationEmailUc;
    private final IForgotPasswordUc forgotPasswordUc;
    private final IFirebaseIdentityUc firebaseIdentityUc;
    private final IVerifyForgotPasswordOtpUc   verifyForgotPasswordOtpUc;
    private final IResetPasswordUc resetPasswordUc;



    @Override
    public SignUpResp SignUp(SignUpReq req) {
        return signUpUC.proccess(req);
    }
    @Override
    public SignInResp SignIn(SignInReq req){
        return signInUC.process(req);
    }

    @Override
    public void resendVerificationEmail(String email) {
        resendVerificationEmailUc.proccess(email);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request){
        return refreshTokenUc.process(request);
    }
    @Override
    public ForgotPasswordRes forgotPassword(ForgotPasswordReq req) {
         return forgotPasswordUc.execute(req);
    }
    @Override
    public FirebaseIdentityUcResp identityWithFirebase(FirebaseSignUpUcRequest req) {
        return firebaseIdentityUc.process(req);
    }
    @Override
    public void resetPassword(String resetToken,ResetPasswordReq req) {
        resetPasswordUc.execute(resetToken,req);
    }

    @Override
    public VerifyForgotPasswordOtpRes verifyForgotPasswordOtp(String recoveryToken, VerifyForgotPasswordOtpReq req) {
        return verifyForgotPasswordOtpUc.process(recoveryToken, req);
    }
}
