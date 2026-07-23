package x10.trainup.cart.infra.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.cart.infra.adapters.mapper.CartMapper;
import x10.trainup.cart.infra.datasources.mongo.CartDocument;
import x10.trainup.cart.infra.datasources.mongo.CartMongoRepository;
import x10.trainup.commons.domain.entities.CartEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RepositoryCartImpl implements IRepositoryCart {

    private final CartMongoRepository mongoRepository;

    @Override
    public CartEntity save(CartEntity cart) {
        CartDocument doc = CartMapper.toDocument(cart);
        CartDocument saved = mongoRepository.save(doc);
        return CartMapper.toEntity(saved);
    }

    @Override
    public Optional<CartEntity> findById(String cartId) {
        return mongoRepository.findById(cartId)
                .map(CartMapper::toEntity);
    }

    @Override
    public Optional<CartEntity> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .map(CartMapper::toEntity);
    }

    @Override
    public void deleteById(String cartId) {
        mongoRepository.deleteById(cartId);
    }

    @Override
    public CartEntity createEmptyCart(String userId) {
        CartEntity cart = CartEntity.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        CartDocument doc = CartMapper.toDocument(cart);
        CartDocument saved = mongoRepository.save(doc);
        return CartMapper.toEntity(saved);
    }
}
