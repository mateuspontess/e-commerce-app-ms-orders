package br.com.ecommerce.orders.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.ecommerce.orders.exception.OutOfStockException;
import br.com.ecommerce.orders.exception.ProductOutOfStockDTO;
import br.com.ecommerce.orders.http.ProductClient;
import br.com.ecommerce.orders.model.order.Order;
import br.com.ecommerce.orders.model.order.OrderBasicInfDTO;
import br.com.ecommerce.orders.model.order.OrderCreateDTO;
import br.com.ecommerce.orders.model.order.OrderDTO;
import br.com.ecommerce.orders.model.order.OrderStatus;
import br.com.ecommerce.orders.model.product.Product;
import br.com.ecommerce.orders.model.product.ProductAndPriceDTO;
import br.com.ecommerce.orders.model.product.ProductDTO;
import br.com.ecommerce.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductClient productClient;

	
	public Order saveOrder(OrderCreateDTO dto, Long userId) {
		// validate stock
		this.validateProductsStocks(dto);
		
		// get products
		List<ProductAndPriceDTO> prices = this.getPricedProducts(dto.products());
		
		Map<Long, Integer> unitsMap = dto.products().stream()
				.collect(Collectors.toMap(ProductDTO::id, ProductDTO::unit));
		List<Product> products = prices.stream()
			.map(p -> new Product(
					p.id(),
					p.price(),
					unitsMap.get(p.id())))
			.toList();
		
		// create order
		Order order = new Order(userId, products);
		// link products to order
		products.forEach(p -> p.setOrder(order));

		orderRepository.save(order);
		return order;
	}
	
	private void validateProductsStocks(OrderCreateDTO dto) {
		ResponseEntity<List<ProductOutOfStockDTO>> response = this.productClient.verifyStocks(dto.products());
		if (response.getStatusCode().equals(HttpStatus.MULTI_STATUS))
			throw new OutOfStockException("There are products out of stock", response.getBody());
		
		if (!response.getStatusCode().equals(HttpStatus.OK))
			throw new RuntimeException("Internal server error");
	}
	private List<ProductAndPriceDTO> getPricedProducts(List<ProductDTO> products) {
		ResponseEntity<List<ProductAndPriceDTO>> response = this.productClient.getPrices(products);
		if (response.getStatusCode().value() != 200)
			throw new RuntimeException("Internal server error");
		
		return response.getBody();
	}
	
	public OrderDTO getOrderById(Long id, Long userId) {
		Order order = orderRepository.findByIdAndUserId(id, userId)
				.orElseThrow(EntityNotFoundException::new);
		
		return new OrderDTO(order);
	}
	
	public Page<OrderBasicInfDTO> getAllOrdersByUser(Pageable pageable, Long userId) {
		return this.orderRepository
				.findAllByUserId(pageable, userId)
				.map(OrderBasicInfDTO::new);
	}
	
	public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
		Order order = orderRepository.getReferenceById(orderId);
		
		order.updateOrderStatus(newStatus);
		return new OrderDTO(order);
	}
}