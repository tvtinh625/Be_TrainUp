package x10.trainup.auth.core.usecases.firebaseIdentity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data

public class FirebaseSignUpUcRequest {

    @NotNull
    private String idToken;
}
