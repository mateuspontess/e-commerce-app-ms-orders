package br.com.ecommerce.orders.model.order;

import java.util.List;

import br.com.ecommerce.orders.model.product.ProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record OrderCreateDTO(@NotEmpty @Valid List<ProductDTO> products) {}