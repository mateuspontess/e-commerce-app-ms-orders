package br.com.ecommerce.orders.exception;

public record ProductOutOfStockDTO(
		Long id, 
		Long productId, 
		String name, 
		Integer unit
		) {}