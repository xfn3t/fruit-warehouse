package com.fruitwarehouse.product.entity;

import com.fruitwarehouse.delivery.entity.DeliveryItem;
import com.fruitwarehouse.supplier.entity.SupplierProductPrice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_type_id", nullable = false)
	private ProductType productType;

	@Column(name = "variety_name", nullable = false, length = 100)
	private String varietyName;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "product")
	private List<SupplierProductPrice> supplierPrices = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	private List<DeliveryItem> deliveryItems = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Product(String name, ProductType productType, String varietyName, String description) {
		this.name = name;
		this.productType = productType;
		this.varietyName = varietyName;
		this.description = description;
	}
}