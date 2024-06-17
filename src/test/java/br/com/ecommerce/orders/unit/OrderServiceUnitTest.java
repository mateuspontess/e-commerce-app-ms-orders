package br.com.ecommerce.orders.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.ecommerce.orders.exception.OutOfStockException;
import br.com.ecommerce.orders.exception.ProductOutOfStockDTO;
import br.com.ecommerce.orders.http.ProductClient;
import br.com.ecommerce.orders.model.order.Order;
import br.com.ecommerce.orders.model.order.OrderCreateDTO;
import br.com.ecommerce.orders.model.order.OrderStatus;
import br.com.ecommerce.orders.model.product.Product;
import br.com.ecommerce.orders.model.product.ProductAndPriceDTO;
import br.com.ecommerce.orders.model.product.ProductDTO;
import br.com.ecommerce.orders.repository.OrderRepository;
import br.com.ecommerce.orders.service.OrderService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

	@Mock
	private OrderRepository repository;
	@Mock
	private ProductClient productClient;
	@InjectMocks
	private OrderService service;


	@Test
	void saveOrderValidateProductsStocksTest01() {
		// arrange
		var input = new OrderCreateDTO(List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100),
			new ProductDTO(3L, 100)
		));

		var responseBodyVerifyStocks = List.of(
			new ProductOutOfStockDTO(1L, "product-1", 1), 
			new ProductOutOfStockDTO(2L, "product-2", 1), 
			new ProductOutOfStockDTO(3L, "product-3", 1));
		
		var responseMock = ResponseEntity.status(HttpStatus.MULTI_STATUS).body(responseBodyVerifyStocks);
		when(productClient.verifyStocks(any())).thenReturn(responseMock);

		// act and assert
		assertThrows(OutOfStockException.class, () -> service.saveOrder(input, 1L));
	}	
	@Test
	void saveOrderValidateProductsStocksTest02() {
		// arrange
		var input = new OrderCreateDTO(List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100),
			new ProductDTO(3L, 100)
		));

		List<ProductOutOfStockDTO> responseBodyVerifyStocks = List.of();
		var responseMock = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBodyVerifyStocks);
		when(productClient.verifyStocks(any())).thenReturn(responseMock);

		// act and assert
		assertThrows(RuntimeException.class, () -> service.saveOrder(input, 1L));
	}
	@Test
	void saveOrdergetPricedProducts01() {
		// arrange
		var input = new OrderCreateDTO(List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100),
			new ProductDTO(3L, 100)
		));

		List<ProductOutOfStockDTO> responseBodyGetPrices = List.of();
		var responseMock = ResponseEntity.status(HttpStatus.OK).body(responseBodyGetPrices);
		when(productClient.verifyStocks(any())).thenReturn(responseMock);

		when(productClient.getPrices(any())).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

		// act and assert
		assertThrows(RuntimeException.class, () -> service.saveOrder(input, 1L));
	}
	@Test
	void saveOrder01() {
		// arrange
		var input = new OrderCreateDTO(List.of(
			new ProductDTO(1L, 100),
			new ProductDTO(2L, 100)
		));

		// simulates that all products have sufficient stock to create the order
		List<ProductOutOfStockDTO> responseBodyVerifyStocks = List.of();
		var responseVerifyStocks = ResponseEntity.status(HttpStatus.OK).body(responseBodyVerifyStocks);
		when(productClient.verifyStocks(any())).thenReturn(responseVerifyStocks);

		// simulates the recovery of product prices
		List<ProductAndPriceDTO> responseBodyGetPrices = List.of(
			new ProductAndPriceDTO(1L, BigDecimal.ONE),
			new ProductAndPriceDTO(2L, BigDecimal.ONE)
		);
		var responseGetPrices = ResponseEntity.status(HttpStatus.OK).body(responseBodyGetPrices);
		when(productClient.getPrices(any())).thenReturn(responseGetPrices);

		// act
		var result = service.saveOrder(input, 1L);

		// assert
		assertEquals(Long.valueOf(1L), result.getUserId());
		assertEquals(200, result.getTotal().intValue());
		assertEquals(OrderStatus.AWAITING_PAYMENT, result.getStatus());
		assertEquals(2, result.getProducts().size());
	}

	@Test
	void getOrderByIdTest01() {
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> service.getOrderById(1L, 1L));
	}
	@Test
	void getOrderByIdTest02() {
		// arrange
		List<Product> products = List.of();
		Order order = Order.builder()
			.id(1L)
			.userId(1L)
			.products(products)
			.total(BigDecimal.ZERO)
			.date(LocalDate.now())
			.status(OrderStatus.AWAITING_PAYMENT)
			.build();
		when(repository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(order));
		
		// act
		var result = service.getOrderById(1L, 1L);

		// assert
		assertEquals(order.getId(), result.id());
	}

	@Test
	void getAllOrdersByUserTest01() {
		// arrange
		List<Product> products = List.of();
		Order order = Order.builder()
			.id(1L)
			.userId(1L)
			.products(products)
			.total(BigDecimal.ZERO)
			.date(LocalDate.now())
			.status(OrderStatus.AWAITING_PAYMENT)
			.build();
		when(repository.findAllByUserId(any(), anyLong())).thenReturn(new PageImpl<>(List.of(order)));
		
		// act
		var result = service.getAllOrdersByUser(Pageable.unpaged(), 1L)
			.getContent()
			.get(0);

		// assert
		assertEquals(order.getId(), result.id());
		assertEquals(order.getTotal(), result.totalOrder());
		assertEquals(order.getDate(), result.date());
		assertEquals(order.getStatus(), result.status());
	}

	@Test
	void updateOrderStatusTest01() {
		// arrange
		List<Product> products = List.of();
		Order order = Order.builder()
			.id(1L)
			.userId(1L)
			.products(products)
			.total(BigDecimal.ZERO)
			.date(LocalDate.now())
			.status(OrderStatus.AWAITING_PAYMENT)
			.build();
		when(repository.getReferenceById(anyLong())).thenReturn(order);
		
		// act
		var result = service.updateOrderStatus(1L, OrderStatus.CANCELED);

		// assert
		assertEquals(OrderStatus.CANCELED, result.status());
	}
}