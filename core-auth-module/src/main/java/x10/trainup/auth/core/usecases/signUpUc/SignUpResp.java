package x10.trainup.auth.core.usecases.signUpUc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResp {
    private String userId;
    private String email; // hoặc username, nếu cần
}
