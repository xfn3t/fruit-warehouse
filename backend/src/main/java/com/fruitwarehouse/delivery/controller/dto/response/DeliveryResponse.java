package com.fruitwarehouse.delivery.controller.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DeliveryResponse(
		Long id,
		UUID deliveryNumber,
		Long supplierId,
		String supplierName,
		LocalDateTime deliveryDate,
		String status,
		LocalDateTime createdAt,
		List<DeliveryItemResponse> items,
		BigDecimal totalWeight,
		BigDecimal totalCost
) {}
