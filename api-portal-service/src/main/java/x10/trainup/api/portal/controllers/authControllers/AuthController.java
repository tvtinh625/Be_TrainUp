package x10.trainup.api.portal.controllers.authControllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import x10.trainup.auth.core.usecases.ICoreAuthService;
import x10.trainup.auth.core.usecases.ResetPasswordUc.ResetPasswordReq;
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
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.mailbox.core.usecases.ICoreMailBoxService;
import x10.trainup.security.core.principal.UserPrincipal;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    @org.springframework.beans.factory.annotation.Value("${app.cookie.access-name:access_token}")
    private String accessCookieName;
    @org.springframework.beans.factory.annotation.Value("${app.cookie.refresh-name:refresh_token}")
    private String refreshCookieName;

    private final ICoreUserSerivce coreUserService;
    private final ICoreAuthService coreAuthService;
    private final ICoreMailBoxService coreMailBoxService;
    private final HttpServletRequest request;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:None}")  // Lax | Strict | None
    private String cookieSameSite;

    @Value("${app.cookie.domain:}") // có thể set .domain.com ở production
    private String cookieDomain;


    private String path() {
        return request.getRequestURI();
    }

    private String traceId() {
        var id = (String) request.getAttribute("traceId");
        return (id != null) ? id : MDC.get("traceId");
    }

    private ResponseCookie buildCookie(String name, String value, long maxAgeSeconds) {
        var builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(maxAgeSeconds);
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }
        return builder.build();
    }

    private void setCookie(HttpServletResponse response, ResponseCookie cookie) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordRes>> forgotPassword(
            @Valid @RequestBody ForgotPasswordReq req) {

        log.info("[AUTH] Forgot password request for email: {}", req.getEmail());

        var resp = coreAuthService.forgotPassword(req);

        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.FORGOT_PASSWORD_SENT",
                "Đã gửi mã OTP đến email của bạn",
                resp, path(), traceId()
        ));
    }

    @PostMapping("/verify-forgot-password-otp")
    public ResponseEntity<ApiResponse<VerifyForgotPasswordOtpRes>> verifyForgotPasswordOtp(
            @RequestHeader("X-Recovery-Token") String recoveryToken,
            @Valid @RequestBody VerifyForgotPasswordOtpReq req) {

        log.info("[AUTH] Verify forgot password OTP for token: {}", recoveryToken);

        var resp = coreAuthService.verifyForgotPasswordOtp(recoveryToken, req);

        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.OTP_VERIFIED",
                "Xác minh OTP thành công. Vui lòng dùng resetToken để đặt lại mật khẩu.",
                resp, path(), traceId()
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
                @RequestHeader("X-Reset-Token") String resetToken,
            @Valid @RequestBody ResetPasswordReq req) {

        log.info("[AUTH] Reset password request using token: {}", resetToken);

        coreAuthService.resetPassword(resetToken, req);

        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.PASSWORD_RESET_SUCCESS",
                "Mật khẩu đã được thay đổi thành công",
                null, path(), traceId()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserEntity>> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of(
                    401, "AUTH.UNAUTHORIZED", "Chưa đăng nhập",
                    null, path(), traceId()
            ));
        }

        return coreUserService.getUserById(user.getId())
                .map(u -> ResponseEntity.ok(ApiResponse.of(
                        200, "USR.FOUND", "Lấy thông tin người dùng thành công",
                        u, path(), traceId()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.of(
                                404, "USR.NOT_FOUND", "Không tìm thấy người dùng",
                                null, path(), traceId()
                        )));
    }

    /**
     * 🔸 Đăng ký tài khoản
     */
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<SignUpResp>> signUp(@Valid @RequestBody SignUpReq req) {
        var resp = coreAuthService.SignUp(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(
                201, "AUTH.SIGN_UP_SUCCESS",
                "Vui lòng kiểm tra email để xác nhận tài khoản",
                resp, path(), traceId()
        ));
    }

    /**
     * 🔸 Xác minh email
     */
    @GetMapping("/verify-email")
    public RedirectView verifyEmail(@RequestParam("token") String token) {
        var resp = coreMailBoxService.confirmEmail(token);
        String redirectUrl = "http://localhost:5173/verify-email?status=" + resp.getStatus();
        return new RedirectView(redirectUrl);
    }

    /**
     * 🔸 Đăng nhập
     */
    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResp>> signIn(
            @Valid @RequestBody SignInReq req,
            HttpServletResponse response) {

        var resp = coreAuthService.SignIn(req);
        setCookie(response, buildCookie(accessCookieName, resp.getAccessToken(), 15 * 60)); // 15 phút
        setCookie(response, buildCookie(refreshCookieName, resp.getRefreshToken(), 7L * 24 * 60 * 60)); // 7 ngày

        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.SIGN_IN_SUCCESS",
                "Đăng nhập thành công",
                resp, path(), traceId()
        ));
    }

    /**
     * 🔸 Gửi lại email xác nhận
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam("email") String email) {
        coreAuthService.resendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.RESEND_EMAIL",
                "Đã gửi lại email xác nhận",
                null, path(), traceId()
        ));
    }

    /**
     * 🔸 Refresh token (làm mới access token)
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest req,
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = (req != null) ? req.getToken() : null;

        if ((refreshToken == null || refreshToken.isBlank()) && request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (refreshCookieName.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of(
                    401, "AUTH.NO_REFRESH_TOKEN",
                    "Thiếu refresh token",
                    null, path(), traceId()
            ));
        }

        var resp = coreAuthService.refreshToken(new RefreshTokenRequest(refreshToken));
        setCookie(response, buildCookie(accessCookieName, resp.getAccessToken(), 15 * 60));
        setCookie(response, buildCookie(refreshCookieName, resp.getRefreshToken(), 7L * 24 * 60 * 60));

        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.REFRESH_TOKEN_SUCCESS",
                "Cấp mới access token thành công",
                resp, path(), traceId()
        ));
    }

    /**
     * 🔸 Đăng xuất
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        setCookie(response, buildCookie(accessCookieName, "", 0));
        setCookie(response, buildCookie(refreshCookieName, "", 0));
        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.LOGOUT_SUCCESS",
                "Đăng xuất thành công",
                null, path(), traceId()
        ));
    }

    /**
     * 🔸 Đăng nhập bằng Firebase (Google)
     */
    @PostMapping("/firebase-sign-in")
    public ResponseEntity<ApiResponse<Object>> firebaseSignIn(
            @Valid @RequestBody FirebaseSignUpUcRequest req,
            HttpServletResponse response) {

        var resp = coreAuthService.identityWithFirebase(req);
        setCookie(response, buildCookie(accessCookieName, resp.getAccessToken(), 15 * 60));
        setCookie(response, buildCookie(refreshCookieName, resp.getRefreshToken(), 7L * 24 * 60 * 60));

        return ResponseEntity.ok(ApiResponse.of(
                200, "AUTH.FIREBASE_SIGN_IN_SUCCESS",
                "Đăng nhập bằng Google thành công",
                resp, path(), traceId()
        ));
    }
}
