package br.com.ecommerce.orders.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ecommerce.orders.model.product.ProductDTO;

public record OrderDTO(
		
		Long id,
		List<ProductDTO> products,
		BigDecimal totalOrder,
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate date,
		OrderStatus status
		) {
	
	public OrderDTO(Order o) {
		this(
				o.getId(),
				o.getProducts().stream().map(p -> new ProductDTO(p)).toList(), 
				o.getTotal(),
				o.getDate(),
				o.getStatus()
			);
	}
}