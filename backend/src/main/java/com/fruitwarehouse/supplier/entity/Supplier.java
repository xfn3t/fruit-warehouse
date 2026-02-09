package com.fruitwarehouse.supplier.entity;

import com.fruitwarehouse.delivery.entity.Delivery;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "contact_email")
	private String contactEmail;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SupplierProductPrice> productPrices = new ArrayList<>();

	@OneToMany(mappedBy = "supplier")
	private List<Delivery> deliveries = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Supplier(String name, String contactEmail, String phoneNumber) {
		this.name = name;
		this.contactEmail = contactEmail;
		this.phoneNumber = phoneNumber;
	}
}
