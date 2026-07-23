package x10.trainup.shop.core.usecases.createShop;

import x10.trainup.commons.domain.entities.ShopEntity;

public interface ICreateShopServiceUc {

    ShopEntity process(CreateShopReq req);
}
