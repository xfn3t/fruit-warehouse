package com.fruitwarehouse.supplier.controller.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PriceResponse(
		Long id,
		Long supplierId,
		Long productId,
		String productName,
		String productType,
		String variety,
		BigDecimal price,
		LocalDate effectiveFrom,
		LocalDate effectiveTo,
		LocalDateTime createdAt
) {}