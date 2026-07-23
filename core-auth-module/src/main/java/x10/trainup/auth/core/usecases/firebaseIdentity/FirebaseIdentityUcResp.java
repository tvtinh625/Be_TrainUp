package x10.trainup.auth.core.usecases.firebaseIdentity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirebaseIdentityUcResp {
    private String id;
    private String username;
    private String accessToken;         // Access Token
    private long refreshExpiresIn;       // số mili giây / giây hết hạn
    private String refreshToken;
}
