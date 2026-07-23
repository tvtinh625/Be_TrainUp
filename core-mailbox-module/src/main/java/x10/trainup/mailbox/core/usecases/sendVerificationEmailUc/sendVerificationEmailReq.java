package x10.trainup.mailbox.core.usecases.sendVerificationEmailUc;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class sendVerificationEmailReq {
    private String email;
    private String username;// optional, có hoặc không cũng được (token thường đã encode email)
    private String token;
}
