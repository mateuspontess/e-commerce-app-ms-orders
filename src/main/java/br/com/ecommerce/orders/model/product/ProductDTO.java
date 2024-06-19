package br.com.ecommerce.orders.model.product;

import jakarta.validation.constraints.NotNull;

public record ProductDTO(
		
	@NotNull
	Long id,
	@NotNull
	Integer unit
	) {
	
	public ProductDTO(Product p) {
		this(p.getProductId(), p.getUnit());
	}
}