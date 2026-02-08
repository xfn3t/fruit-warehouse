package com.fruitwarehouse.delivery.controller.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreateDeliveryRequest(
		@NotNull(message = "Supplier ID is required")
		Long supplierId,

		LocalDateTime deliveryDate,

		@NotNull(message = "Delivery items are required")
		@Size(min = 1, message = "At least one delivery item is required")
		@Valid
		List<DeliveryItemRequest> items
) {}
