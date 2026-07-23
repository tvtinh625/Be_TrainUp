package x10.trainup.shop.infra.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.trainup.commons.domain.entities.ShopEntity;
import x10.trainup.shop.core.repository.IRepositoryShop;
import x10.trainup.shop.infra.adapters.mapper.ShopMapper;
import x10.trainup.shop.infra.datasoucre.mongo.MongoShopReposity;
import x10.trainup.shop.infra.datasoucre.mongo.ShopDocument;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReposityShopImpl implements IRepositoryShop {

    private final MongoShopReposity mongoShopReposity;

    @Override
    public ShopEntity save(ShopEntity shopEntity) {
        ShopDocument document = ShopMapper.toDocument(shopEntity);
        ShopDocument savedDocument = mongoShopReposity.save(document);
        return ShopMapper.toEntity(savedDocument);
    }

    @Override
    public Optional<ShopEntity> findById(String id) {
        return mongoShopReposity.findById(id)
                .map(ShopMapper::toEntity);
    }

    @Override
    public List<ShopEntity> findAll() {
        return mongoShopReposity.findAll()
                .stream()
                .map(ShopMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoShopReposity.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoShopReposity.existsById(id);
    }

    @Override
    public Optional<ShopEntity> findByName(String name) {
        return mongoShopReposity.findByName(name)
                .map(ShopMapper::toEntity);
    }

    @Override
    public boolean existsByName(String name) {
        return mongoShopReposity.existsByName(name);
    }
}