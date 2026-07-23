package x10.trainup.cart.core.usecases.createCartUc;

import x10.trainup.commons.domain.entities.CartEntity;

public interface ICreateCartUc {
    CreateCartRes addToCart(CreateCartReq createCartReq,String userId);
}
