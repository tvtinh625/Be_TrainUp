package x10.trainup.auth.core.usecases.signInUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInReq {
    private String email;     // hoặc username
    private String password;
}
