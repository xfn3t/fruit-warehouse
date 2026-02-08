package com.fruitwarehouse.delivery.entity;

import com.fruitwarehouse.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_items")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "delivery_id", nullable = false)
	private Delivery delivery;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "weight", columnDefinition = "fruitwarehouse.weight_domain", nullable = false)
	private BigDecimal weight;

	@Column(name = "unit_price", columnDefinition = "fruitwarehouse.price_domain", nullable = false)
	private BigDecimal unitPrice;

	@Column(name = "total_price", columnDefinition = "fruitwarehouse.price_domain", nullable = false)
	private BigDecimal totalPrice;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		calculateTotalPrice();
	}

	@PreUpdate
	protected void onUpdate() {
		calculateTotalPrice();
	}

	public void calculateTotalPrice() {
		if (weight != null && unitPrice != null) {
			totalPrice = weight.multiply(unitPrice).setScale(2, java.math.RoundingMode.HALF_UP);
		}
	}
}
