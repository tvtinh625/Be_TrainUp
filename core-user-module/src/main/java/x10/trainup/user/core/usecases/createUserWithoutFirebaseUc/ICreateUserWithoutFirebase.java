package x10.trainup.user.core.usecases.createUserWithoutFirebaseUc;

import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.user.core.usecases.createUser.CreateUserReq;

public interface ICreateUserWithoutFirebase {

    UserEntity process(CreateUserFireBaseReq user);
}
