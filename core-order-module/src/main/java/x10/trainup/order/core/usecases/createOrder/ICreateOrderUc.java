package x10.trainup.order.core.usecases.createOrder;

import x10.trainup.commons.domain.entities.OrderEntity;

public interface ICreateOrderUc {

    OrderEntity process(CreateOrderReq req);
}
