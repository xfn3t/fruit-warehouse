package com.fruitwarehouse.supplier.controller.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePriceRequest(
		@NotNull(message = "Product ID is required")
		Long productId,

		@NotNull(message = "Price is required")
		@DecimalMin(value = "0.01", message = "Price must be greater than 0")
		BigDecimal price,

		@NotNull(message = "Effective from date is required")
		LocalDate effectiveFrom,

		LocalDate effectiveTo
) {}