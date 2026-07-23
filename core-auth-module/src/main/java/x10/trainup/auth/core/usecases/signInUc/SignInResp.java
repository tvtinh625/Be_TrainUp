package x10.trainup.auth.core.usecases.signInUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.entities.RoleEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInResp {
    private String id;
    private String username;
    private String accessToken;         // Access Token
    private long refreshExpiresIn;       // số mili giây / giây hết hạn
    private String refreshToken;
    private List<RoleEntity> roles;
}
