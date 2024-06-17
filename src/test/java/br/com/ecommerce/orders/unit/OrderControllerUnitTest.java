package br.com.ecommerce.orders.unit;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.orders.model.order.Order;
import br.com.ecommerce.orders.model.order.OrderCreateDTO;
import br.com.ecommerce.orders.model.order.OrderStatus;
import br.com.ecommerce.orders.model.product.Product;
import br.com.ecommerce.orders.model.product.ProductDTO;
import br.com.ecommerce.orders.service.OrderService;

@WebMvcTest
@AutoConfigureJsonTesters
class OrderControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService service; 
    @MockBean
    private RabbitTemplate template;

    @Autowired
    private JacksonTester<OrderCreateDTO> orderCreateDTOJson;


    @Test
    @DisplayName("Create order - Should return a response containing the created order details")
    void createTest() throws IOException, Exception {
        // arrange
        var productsList = List.of(
            new ProductDTO(1L, 100),
            new ProductDTO(2L, 100),
            new ProductDTO(3L, 100)
        );
        OrderCreateDTO requestBody = new OrderCreateDTO(productsList);
        
        Order mockServiceReturn = 
            new Order(1L, productsList.stream().map(p -> new Product(p.id(), BigDecimal.ONE, p.unit())).toList());
        when(service.saveOrder(any(), any())).thenReturn(mockServiceReturn);

        // act and assert
        mvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(orderCreateDTOJson.write(requestBody).getJson())
            .header("X-auth-user-id", "1")
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.products").isNotEmpty())
            .andExpect(jsonPath("$.products", hasSize(3)))
            .andExpect(jsonPath("$.totalOrder").value(300))
            .andExpect(jsonPath("$.date").isNotEmpty())
            .andExpect(jsonPath("$.status").value(OrderStatus.AWAITING_PAYMENT.toString()));
    }

    @Test
    @DisplayName("Cancel order - Should return status 200")
    void cancelOrderTest() throws IOException, Exception {
        mvc.perform(patch("/orders/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-auth-user-id", "1")
        ).andExpect(status().isOk());
    }
}