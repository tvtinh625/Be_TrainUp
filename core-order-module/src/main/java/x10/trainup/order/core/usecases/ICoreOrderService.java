package x10.trainup.order.core.usecases;

import x10.trainup.commons.domain.entities.OrderEntity;
import x10.trainup.order.core.usecases.createOrder.CreateOrderReq;

import java.util.List;


public interface ICoreOrderService {
    OrderEntity createOrder(CreateOrderReq req);
    List<OrderEntity> getAllOrdersSortedByDateDesc();

    List<OrderEntity> getOrdersByUserId(String userId);

    List<OrderEntity> getOrdersByGuestEmail(String email);
    
    OrderEntity updateOrderStatus(UpdateOrderStatusReq req);

    java.util.Optional<OrderEntity> getOrderById(String orderId);
}