package x10.trainup.order.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum OrderError implements ErrorDescriptor {

    // 🛒 Order Management
    ORDER_NOT_FOUND(404, "ORD.NOT_FOUND", "Order not found"),
    ORDER_ALREADY_EXISTS(409, "ORD.ALREADY_EXISTS", "Order already exists"),
    ORDER_CANNOT_BE_UPDATED(400, "ORD.CANNOT_UPDATE", "Order cannot be updated in current status"),
    ORDER_CANNOT_BE_CANCELLED(400, "ORD.CANNOT_CANCEL", "Order cannot be cancelled in current status"),
    SHIPPING_FEE_CALCULATION_FAILED(500, "ORDER.SHIPPING_FEE_FAILED", "Failed to calculate shipping fee"),
    // 📦 Order Items
    ORDER_ITEM_NOT_FOUND(404, "ORD.ITEM_NOT_FOUND", "Order item not found"),
    INVALID_ORDER_ITEM(400, "ORD.INVALID_ITEM", "Invalid order item"),

    // 💰 Payment
    PAYMENT_FAILED(500, "ORD.PAYMENT_FAILED", "Payment processing failed"),
    PAYMENT_ALREADY_PROCESSED(400, "ORD.PAYMENT_ALREADY_PROCESSED", "Payment already processed"),

    // 🚚 Shipping
    INVALID_SHIPPING_ADDRESS(400, "ORD.INVALID_SHIPPING", "Invalid shipping address"),
    SHIPPING_FAILED(500, "ORD.SHIPPING_FAILED", "Shipping failed"),

    // 🔄 Status Transitions
    INVALID_STATUS_TRANSITION(400, "ORD.INVALID_STATUS", "Invalid order status transition");

    private final int http;
    private final String code;
    private final String msg;

    OrderError(int http, String code, String msg) {
        this.http = http;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int httpStatus() {
        return http;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return msg;
    }
}