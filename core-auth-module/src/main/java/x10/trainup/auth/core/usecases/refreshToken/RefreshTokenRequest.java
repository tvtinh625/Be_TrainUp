package x10.trainup.auth.core.usecases.refreshToken;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RefreshTokenRequest {
    @JsonProperty("token")
    @JsonAlias({"refreshToken", "refresh_token"})
    private String token;
}
