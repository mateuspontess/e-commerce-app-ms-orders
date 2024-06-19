package br.com.ecommerce.orders.unit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.ecommerce.orders.model.order.Order;
import br.com.ecommerce.orders.model.order.OrderStatus;
import br.com.ecommerce.orders.model.product.Product;

class OrderUnitTest {

    @Test
    @DisplayName("Test creating order with valid data")
    void createOrderTest01() {
        assertDoesNotThrow(() -> new Order(1L, List.of(new Product(1L, BigDecimal.TEN, 1))),
            "Should not throw exception when creating order with valid data");
            
        assertAll(() -> {
            var products = List.of(new Product(1L, BigDecimal.ONE, 1), new Product(2L, BigDecimal.ONE, 1), new Product(3L, BigDecimal.ONE, 1));
            var order = new Order(1L, products);

            assertEquals(BigDecimal.valueOf(3), order.getTotal());
        });
    }
    @Test
    @DisplayName("Test creating order with invalid data")
    void createOrderTest02() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Order(null, List.of(new Product(1L, BigDecimal.TEN, 1))),
            "Should throw IllegalArgumentException when userId is null");
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Order(null, List.of()),
            "Should throw IllegalArgumentException when userId is null and products list is empty");
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Order(null, null),
            "Should throw IllegalArgumentException when userId is null and products list is null");
    }
    
    @Test
    @DisplayName("Test updating order status with valid and invalid transitions")
    void updateOrderStatusTest01() {
        // arrange
        Order target = Order.builder()
            .status(OrderStatus.AWAITING_PAYMENT)
            .build();

        Order target2 = Order.builder()
            .status(OrderStatus.AWAITING_PAYMENT)
            .build();

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.AWAITING_PAYMENT),
            "Should throw exception when attempting to transition to the same status");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.DELIVERED),
            "Should throw exception when transitioning from AWAITING_PAYMENT to DELIVERED");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.IN_TRANSIT),
            "Should throw exception when transitioning from AWAITING_PAYMENT to IN_TRANSIT");

        assertDoesNotThrow(() -> target.updateOrderStatus(OrderStatus.CONFIRMED_PAYMENT),
            "Should not throw exception when transitioning from AWAITING_PAYMENT to CONFIRMED_PAYMENT");

        assertDoesNotThrow(() -> target2.updateOrderStatus(OrderStatus.CANCELED),
            "Should not throw exception when transitioning from AWAITING_PAYMENT to CANCELED");
    }
    @Test
    @DisplayName("Test updating order status from CONFIRMED_PAYMENT")
    void updateOrderStatusTest02() {
        // arrange
        Order target = Order.builder()
            .status(OrderStatus.CONFIRMED_PAYMENT)
            .build();

        Order target2 = Order.builder()
            .status(OrderStatus.CONFIRMED_PAYMENT)
            .build();

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.AWAITING_PAYMENT),
            "Should throw exception when transitioning from CONFIRMED_PAYMENT to AWAITING_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.CONFIRMED_PAYMENT),
            "Should throw exception when transitioning from CONFIRMED_PAYMENT to CONFIRMED_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.DELIVERED),
            "Should throw exception when transitioning from CONFIRMED_PAYMENT to DELIVERED");

        assertDoesNotThrow(() -> target.updateOrderStatus(OrderStatus.IN_TRANSIT),
            "Should not throw exception when transitioning from CONFIRMED_PAYMENT to IN_TRANSIT");

        assertDoesNotThrow(() -> target2.updateOrderStatus(OrderStatus.CANCELED),
            "Should not throw exception when transitioning from CONFIRMED_PAYMENT to CANCELED");
    }
    @Test
    @DisplayName("Test updating order status from IN_TRANSIT")
    void updateOrderStatusTest03() {
        // arrange
        Order target = Order.builder()
            .status(OrderStatus.IN_TRANSIT)
            .build();

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.AWAITING_PAYMENT),
            "Should throw exception when transitioning from IN_TRANSIT to AWAITING_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.CONFIRMED_PAYMENT),
            "Should throw exception when transitioning from IN_TRANSIT to CONFIRMED_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.IN_TRANSIT),
            "Should throw exception when transitioning from IN_TRANSIT to IN_TRANSIT");

        assertDoesNotThrow(() -> target.updateOrderStatus(OrderStatus.DELIVERED),
            "Should not throw exception when transitioning from IN_TRANSIT to DELIVERED");
    }
    @Test
    @DisplayName("Test updating order status from DELIVERED")
    void updateOrderStatusTest04() {
        // arrange
        Order target = Order.builder()
            .status(OrderStatus.DELIVERED)
            .build();

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.AWAITING_PAYMENT),
            "Should throw exception when transitioning from DELIVERED to AWAITING_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.CONFIRMED_PAYMENT),
            "Should throw exception when transitioning from DELIVERED to CONFIRMED_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.IN_TRANSIT),
            "Should throw exception when transitioning from DELIVERED to IN_TRANSIT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.DELIVERED),
            "Should throw exception when transitioning from DELIVERED to DELIVERED");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.CANCELED),
            "Should throw exception when transitioning from DELIVERED to CANCELED");
    }
    @Test
    @DisplayName("Test updating order status from CANCELED")
    void updateOrderStatusTest05() {
        // arrange
        Order target = Order.builder()
            .status(OrderStatus.CANCELED)
            .build();

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.AWAITING_PAYMENT),
            "Should throw exception when transitioning from CANCELED to AWAITING_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.CONFIRMED_PAYMENT),
            "Should throw exception when transitioning from CANCELED to CONFIRMED_PAYMENT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.IN_TRANSIT),
            "Should throw exception when transitioning from CANCELED to IN_TRANSIT");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.DELIVERED),
            "Should throw exception when transitioning from CANCELED to DELIVERED");

        assertThrows(IllegalArgumentException.class, () -> target.updateOrderStatus(OrderStatus.CANCELED),
            "Should throw exception when transitioning from CANCELED to CANCELED");
    }
}