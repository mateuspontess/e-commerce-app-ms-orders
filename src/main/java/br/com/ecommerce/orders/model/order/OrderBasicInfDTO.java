package br.com.ecommerce.orders.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderBasicInfDTO(
		
		Long id,
		
		BigDecimal totalOrder,
		
		@JsonFormat(pattern = "dd/MM/yyyy")
		LocalDate date,
		
		OrderStatus status
		) {
	
	public OrderBasicInfDTO(Order o) {
		this(
			o.getId(),
			o.getTotal(),
			o.getDate(),
			o.getStatus()
		);
	}
}