package x10.trainup.user.core.usecases.createGuestUserUc;

import x10.trainup.commons.domain.entities.UserEntity;

public interface ICreateGuestUserUc {
    UserEntity execute(CreateGuestUserReq req);
}