package br.com.ecommerce.orders.http;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.ecommerce.orders.exception.ProductOutOfStockDTO;
import br.com.ecommerce.orders.model.product.ProductAndPriceDTO;
import br.com.ecommerce.orders.model.product.ProductDTO;

@FeignClient(value = "products-ms")
public interface ProductClient {

	@PostMapping(
		value = "/products/stocks",
		headers = {"Content-Type: application/json"})
	ResponseEntity<List<ProductOutOfStockDTO>> verifyStocks(@RequestBody List<ProductDTO> products);

	@PostMapping(
		value = "/products/prices",
		headers = {"Content-Type: application/json"})
	ResponseEntity<List<ProductAndPriceDTO>> getPrices(@RequestBody List<Long> productsIds);
}