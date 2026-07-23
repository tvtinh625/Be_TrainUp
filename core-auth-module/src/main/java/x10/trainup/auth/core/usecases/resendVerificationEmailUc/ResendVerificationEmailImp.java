package x10.trainup.auth.core.usecases.resendVerificationEmailUc;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.datasources.redis.ITokenStore;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.mailbox.core.usecases.ICoreMailBoxService;
import x10.trainup.mailbox.core.usecases.sendVerificationEmailUc.sendVerificationEmailReq;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.repositories.IUserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ResendVerificationEmailImp implements IResendVerificationEmailUc {

    private static final long VerificationTokenTTL = 5 * 60 * 1000; // 5 minutes in milliseconds
    private final ICoreMailBoxService mailboxService;
    private final IUserRepository iUserRepository;
    private final IJwtService jwtService;
    private final ITokenStore tokenStore;
    @Override
    public void proccess(String email) {

        UserEntity user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(UserError.USER_NOT_FOUND,
                        "User with email " + email + " not found"));

        if (user.isEmailVerified()) {
            throw new BusinessException(UserError.EMAIL_ALREADY_VERIFIED,
                    "Email " + email + " already verified");
        }

      

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getEmail());
        mailboxService.sendVerificationEmail(sendVerificationEmailReq.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build()
        );
        tokenStore.saveToken("verify:" +user.getId(), token, VerificationTokenTTL);

    }


}
