package x10.trainup.user.core.usecases.updateStatusUserUc;

import x10.trainup.commons.domain.entities.UserEntity;

public interface IUpdateUserStatusUc {

    UserEntity process(String userId, UpdateUserStatusReq req);

}
