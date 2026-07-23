package x10.trainup.order.core.repositories;

import x10.trainup.commons.domain.entities.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository {

    OrderEntity save(OrderEntity order);

    Optional<OrderEntity> findById(String id);

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    List<OrderEntity> findAll();

    List<OrderEntity> findByUserId(String userId);

    List<OrderEntity> findByShippingEmail(String email);

    void deleteById(String id);

    boolean existsById(String id);

    List<OrderEntity> findAllOrderByCreatedAtDesc();
}