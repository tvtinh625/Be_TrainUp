package x10.trainup.auth.core.usecases.firebaseIdentity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.repositories.IUserRepository;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.user.core.usecases.createUser.CreateUserReq;
import x10.trainup.user.core.usecases.createUserWithoutFirebaseUc.CreateUserFireBaseReq;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class FirebaseIdentityUcImpl implements IFirebaseIdentityUc {

    private final ICoreUserSerivce coreUserSerivce;
    private final IUserRepository userRepository;
    private final IJwtService jwtService;

    @Override
    public FirebaseIdentityUcResp process(FirebaseSignUpUcRequest req) {
        try {
            // 1️⃣ Xác thực idToken từ Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(req.getIdToken());
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String firebaseUid = decodedToken.getUid();
            String avatarUrl = (String) decodedToken.getClaims().get("picture");

            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = coreUserSerivce.createUserWithoutFirebase(CreateUserFireBaseReq.builder()
                                .username(name != null ? name : email)
                                .email(email)
                                .password(UUID.randomUUID().toString())
                                .phone(null)
                                .avatarUrl(avatarUrl)
                                .providerId(firebaseUid)
                                .build()
                );
            }
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return FirebaseIdentityUcResp.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Firebase authentication failed: " + e.getMessage());
        }
    }
}
