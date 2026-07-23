package x10.trainup.auth.core.usecases.refreshToken;

public interface IRefreshTokenUc {
    RefreshTokenResponse process(RefreshTokenRequest request);
}
