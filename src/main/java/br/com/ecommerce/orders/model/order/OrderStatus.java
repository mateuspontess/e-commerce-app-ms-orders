package br.com.ecommerce.orders.model.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {

    AWAITING_PAYMENT("awaiting_payment"),
    CONFIRMED_PAYMENT("confirmed_payment"),
    IN_TRANSIT("in_transit"),
    DELIVERED("delivered"),
    CANCELED("canceled");

    private String status;

    OrderStatus(String status) {
        this.status = status.toUpperCase();
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static OrderStatus fromString(String value) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.status.equalsIgnoreCase(value)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Invalid order status value: " + value);
    }
}