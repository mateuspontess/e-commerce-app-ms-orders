package br.com.ecommerce.orders.model.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.orders.model.product.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity(name = "Order")
@Table(name = "orders")
public class Order {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long userId;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products = new ArrayList<Product>();
	
	private BigDecimal total;
	
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	
	public Order(Long userId, List<Product> products) {
		this.checkNotNull(userId, "userId");

		if (products == null || products.isEmpty())
			throw new IllegalArgumentException("Cannot me null or empty: products");

		this.products = products;
		this.userId = userId;

		this.status = OrderStatus.AWAITING_PAYMENT;
		this.total = this.calculateTotalOrderValue();
		this.date = LocalDate.now();
	}

	public void updateOrderStatus(OrderStatus newStatus) {
		if(!this.isValidStatusTransition(newStatus))
			throw new IllegalArgumentException("The status " + this.status + " cannot transition to " + newStatus);
		this.status = newStatus;
	}
	
	private BigDecimal calculateTotalOrderValue() {
		return this.products.stream()
			.map(p -> p.getPrice().multiply(new BigDecimal(p.getUnit())))
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	private void checkNotNull(Object field, String fieldName) {
		if (field == null)
			throw new IllegalArgumentException("Cannot be null: " + fieldName); 
	}

	private boolean isValidStatusTransition(OrderStatus newStatus) {
		if (this.status == OrderStatus.AWAITING_PAYMENT) {
			return List.of(OrderStatus.CONFIRMED_PAYMENT, OrderStatus.CANCELED)
				.contains(newStatus);
		}
		if (this.status == OrderStatus.CONFIRMED_PAYMENT) {
			return List.of(OrderStatus.IN_TRANSIT, OrderStatus.CANCELED)
				.contains(newStatus);
		}
		if (this.status == OrderStatus.IN_TRANSIT) {
			return List.of(OrderStatus.DELIVERED)
				.contains(newStatus);
		}
		
		if (this.status == OrderStatus.DELIVERED) {
			return false;
		}
		if (this.status == OrderStatus.CANCELED) {
			return false;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("Order[userId=%d, total=%s, status=%s]", this.userId, this.total.toString(), this.status);
	}
}