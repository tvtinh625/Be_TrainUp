package x10.trainup.shop.core.usecases;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.ShopEntity;
import x10.trainup.shop.core.repository.IRepositoryShop;
import x10.trainup.shop.core.usecases.createShop.CreateShopReq;
import x10.trainup.shop.core.usecases.createShop.ICreateShopServiceUc;

import java.util.List;

@Service
@AllArgsConstructor
public class CoreShopServiceImpl implements  ICoreShopServiceUc {

    private final ICreateShopServiceUc iCreateShopServiceUc;
    private final IRepositoryShop iRepositoryShop;
    @Override
    public ShopEntity create(CreateShopReq req){
            return iCreateShopServiceUc.process(req);
    }

    @Override
    public List<ShopEntity> getAll(){
        return iRepositoryShop.findAll();
    }
}
