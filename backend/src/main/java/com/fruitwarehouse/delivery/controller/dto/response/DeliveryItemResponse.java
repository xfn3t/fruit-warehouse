package com.fruitwarehouse.delivery.controller.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DeliveryItemResponse(
		Long id,
		Long productId,
		String productName,
		String productType,
		String variety,
		BigDecimal weight,
		BigDecimal unitPrice,
		BigDecimal totalPrice
) {}
