package com.fruitwarehouse.delivery.controller.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DeliveryItemRequest(
		@NotNull(message = "Product ID is required")
		Long productId,

		@NotNull(message = "Weight is required")
		@DecimalMin(value = "0.001", message = "Weight must be greater than 0")
		BigDecimal weight
) {}
