package x10.trainup.mailbox.core.usecases.resendVerificationEmailUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.datasources.redis.ITokenStore;
import x10.trainup.mailbox.core.usecases.sendVerificationEmailUc.sendVerificationEmailReq;
import x10.trainup.security.core.jwt.IJwtService;

@Service
@AllArgsConstructor
public class ResendVerificationEmailImpl implements  IResendVerificationEmailUc{

    private final IJwtService jwtService;
    private final ITokenStore tokenStore;

    private static final long VerificationTokenTTL = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    public void proccess(sendVerificationEmailReq req) {



    }

}
