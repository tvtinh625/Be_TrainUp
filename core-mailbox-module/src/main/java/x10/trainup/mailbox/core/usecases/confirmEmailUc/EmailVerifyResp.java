package x10.trainup.mailbox.core.usecases.confirmEmailUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.EmailVerifyStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVerifyResp {
    private EmailVerifyStatus status;
    private String message;
}
