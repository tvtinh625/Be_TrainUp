package x10.trainup.user.core.usecases.updateStatusUserUc;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.UserStatus;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  UpdateUserStatusReq {

    @NotNull(message = "Status cannot be null")
    private UserStatus status;

    private String reason;
}