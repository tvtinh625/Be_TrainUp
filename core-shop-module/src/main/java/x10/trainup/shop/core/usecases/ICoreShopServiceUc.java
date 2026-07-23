package x10.trainup.shop.core.usecases;

import x10.trainup.commons.domain.entities.ShopEntity;
import x10.trainup.shop.core.usecases.createShop.CreateShopReq;

import java.util.List;

public interface ICoreShopServiceUc {

    ShopEntity create(CreateShopReq req);

    List<ShopEntity> getAll();


}

