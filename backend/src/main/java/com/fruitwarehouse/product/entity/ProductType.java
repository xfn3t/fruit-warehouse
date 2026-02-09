package com.fruitwarehouse.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_types")
@Getter
@Setter
@NoArgsConstructor
public class ProductType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, unique = true, length = 50)
	@Enumerated(EnumType.STRING)
	private Code code;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	public enum Code {
		APPLE,
		PEAR
	}

	public ProductType(Code code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.isActive = true;
	}
}