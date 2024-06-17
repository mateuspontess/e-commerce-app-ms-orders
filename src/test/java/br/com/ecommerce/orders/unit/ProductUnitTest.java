package br.com.ecommerce.orders.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.ecommerce.orders.model.order.Order;
import br.com.ecommerce.orders.model.product.Product;

class ProductUnitTest {

    @Test
    @DisplayName("Test creating an order with a valid product")
    void createOrderTest01() {
        assertDoesNotThrow(() -> new Order(1L, List.of(new Product(1L, BigDecimal.TEN, 1))));
    }
    
    @Test
    @DisplayName("Test creating product with various invalid inputs")
    void createOrderTest02() {
        assertDoesNotThrow(() -> new Order(1L, List.of(new Product(1L, BigDecimal.TEN, 1))),
            "Creating order with valid inputs should not throw an exception");
    
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(null, BigDecimal.TEN, 10),
            "Creating product with null ID should throw IllegalArgumentException");
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(1L, null, 10),
            "Creating product with null price should throw IllegalArgumentException");
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(1L, BigDecimal.TEN, null),
            "Creating product with null quantity should throw IllegalArgumentException");
    
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(null, null, 10),
            "Creating product with null ID and null price should throw IllegalArgumentException");
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(1L, null, null),
            "Creating product with null price and null quantity should throw IllegalArgumentException");
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(null, BigDecimal.TEN, null),
            "Creating product with null ID and null quantity should throw IllegalArgumentException");
    
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(null, null, null),
            "Creating product with all null inputs should throw IllegalArgumentException");
    }
}