package br.com.ecommerce.orders.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.orders.model.order.Order;
import br.com.ecommerce.orders.model.order.OrderCreateDTO;
import br.com.ecommerce.orders.model.order.OrderDTO;
import br.com.ecommerce.orders.model.product.Product;
import br.com.ecommerce.orders.model.product.ProductDTO;
import br.com.ecommerce.orders.service.OrderService;

@WebMvcTest
@AutoConfigureJsonTesters
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService service; 
    @MockBean
    private RabbitTemplate template;

    // json testers
    @Autowired
    private JacksonTester<OrderCreateDTO> orderCreateDTOJson;
    @Autowired
    private JacksonTester<OrderDTO> orderDTOJson;

    @Test
    @DisplayName("Create order - Should return a response containing the created order details")
    void createTest() throws IOException, Exception {
        // arrange
        var productsList = List.of(
            new ProductDTO(1L, 100),
            new ProductDTO(2L, 200),
            new ProductDTO(3L, 300)
        );
        OrderCreateDTO requestBody = new OrderCreateDTO(productsList);
        
        Order mockServiceReturn = 
            new Order(1L, productsList.stream().map(p -> new Product(p.id(), BigDecimal.ONE, p.unit())).toList());
        when(service.saveOrder(any(), any())).thenReturn(mockServiceReturn);

        // act
        var result = mvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderCreateDTOJson.write(requestBody).getJson())
            .header("X-auth-user-id", "1")
            ).andReturn().getResponse();
        
        var responseBody = orderDTOJson.parseObject(result.getContentAsString());
        System.out.println(responseBody);
        
        // assert
        assertNotNull(responseBody, "The response body should not be null");
        assertNotNull(responseBody.date(), "The date should not be null");
        assertNotNull(responseBody.totalOrder(), "The totalOrder should not be null");
        assertNotNull(responseBody.status(), "The status should not be null");
        assertEquals(3, responseBody.products().size(), "The number of products in the response should be 3");
        assertEquals(requestBody.products(), responseBody.products(), "The response body should match the request body");
    }

    @Test
    @DisplayName("Cancel order - Should return status 200")
    void cancelOrderTest() throws IOException, Exception {
        // Act
        var result = mvc.perform(patch("/orders/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", "1")
        ).andReturn().getResponse();
        
        // Assert
        assertEquals(HttpStatus.OK.value(), result.getStatus(), "The response status should be 200 (OK)");
    }
}