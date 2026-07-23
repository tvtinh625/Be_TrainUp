package x10.trainup.user.core.usecases.updateUser;

import x10.trainup.commons.domain.entities.UserEntity;

public interface IUpdateUserUc {

    UserEntity process(String userId, UpdateUserReq user);
}
