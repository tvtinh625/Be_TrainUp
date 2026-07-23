package x10.trainup.user.core.usecases.createUser;

import x10.trainup.commons.domain.entities.UserEntity;

public interface ICreateUserUc {
    UserEntity process(CreateUserReq user);
}
