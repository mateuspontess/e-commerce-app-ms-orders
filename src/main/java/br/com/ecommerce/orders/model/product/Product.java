package br.com.ecommerce.orders.model.product;

import java.math.BigDecimal;

import br.com.ecommerce.orders.model.order.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity(name = "Product")
@Table(name = "products")
public class Product {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Setter
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "order_id") 
	private Order order;
	
	private Long productId;
	private Integer unit;
	private BigDecimal price;
	
	public Product(Long productId, BigDecimal price, Integer unit) {
		checkNotNull(productId, "productId");
        checkNotNull(price, "price");
        checkNotNull(unit, "unit");

		this.productId = productId;
		this.unit = unit;
		this.price = price;
	}

    private void checkNotNull(Object field, String fieldName) {
        if (field == null)
            throw new IllegalArgumentException("Cannot be null: " + fieldName);
	}
}