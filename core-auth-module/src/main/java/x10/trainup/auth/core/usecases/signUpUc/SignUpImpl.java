package x10.trainup.auth.core.usecases.signUpUc;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import x10.trainup.commons.datasources.redis.ITokenStore;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.user.core.usecases.createUser.CreateUserReq;
import x10.trainup.mailbox.core.usecases.ICoreMailBoxService;
import x10.trainup.mailbox.core.usecases.sendVerificationEmailUc.sendVerificationEmailReq;

@Service
@AllArgsConstructor
public class SignUpImpl implements ISignUpUc {

    private final ICoreUserSerivce userSerivce;
    private final ICoreMailBoxService mailboxService;
    private final IJwtService jwtService;
    private final ITokenStore tokenStore;

    private static final long VerificationTokenTTL = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    public SignUpResp proccess(SignUpReq req) {
        UserEntity newUser = userSerivce.createUser(
            CreateUserReq.builder()
                    .username(req.getUsername())
                    .password(req.getPassword())
                    .email(req.getEmail())
                    .phone(req.getPhone())
                    .build()
        );

        String token = jwtService.generateToken(newUser.getId(), newUser.getUsername(), newUser.getEmail());
        System.out.println("==== DEBUG SIGN UP ====");
        System.out.println("Req email: " + req.getEmail());
        System.out.println("NewUser email: " + newUser.getEmail());


        mailboxService.sendVerificationEmail(sendVerificationEmailReq.builder()
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .token(token)
                .build()
        );
//        tokenStore.saveToken("verify:" + newUser.getId(), token, 900000);
        tokenStore.saveToken("verify:" + newUser.getId(), token, VerificationTokenTTL);

        return new SignUpResp(
                newUser.getId(),
                newUser.getEmail()
                // userId
        );
    }
}
