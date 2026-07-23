package x10.trainup.order.infra.adapters.repositories;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.trainup.commons.domain.entities.OrderEntity;
import x10.trainup.order.core.repositories.IOrderRepository;
import x10.trainup.order.infra.adapters.repositories.mapper.OrderMapper;
import x10.trainup.order.infra.datasources.mongo.MongoOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class OrderRepositoryImpl implements IOrderRepository {

    private final MongoOrderRepository mongoOrderRepository;

    @Override
    public OrderEntity save(OrderEntity order) {
        var document = mongoOrderRepository.save(OrderMapper.toDocument(order));
        return OrderMapper.toEntity(document);
    }

    @Override
    public Optional<OrderEntity> findById(String id) {
        return mongoOrderRepository.findById(id)
                .map(OrderMapper::toEntity);
    }

    @Override
    public Optional<OrderEntity> findByOrderNumber(String orderNumber) {
        return mongoOrderRepository.findByOrderNumber(orderNumber)
                .map(OrderMapper::toEntity);
    }

    @Override
    public List<OrderEntity> findAll() {
        return mongoOrderRepository.findAll().stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderEntity> findByUserId(String userId) {
        return mongoOrderRepository.findByUserId(userId).stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderEntity> findByShippingEmail(String email) {
        return mongoOrderRepository.findByShippingAddressEmailOrderByCreatedAtDesc(email).stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderEntity> findAllOrderByCreatedAtDesc() {
        return mongoOrderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoOrderRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoOrderRepository.existsById(id);
    }
}