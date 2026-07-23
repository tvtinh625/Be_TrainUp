package x10.trainup.auth.core.usecases.refreshToken;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class RefreshTokenResponse {
    private String accessToken;         // Access Token
    private String refreshToken;
}
