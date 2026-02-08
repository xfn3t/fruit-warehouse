package com.fruitwarehouse.report.controller.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DetailedReportItemResponse(
		String supplierName,
		UUID deliveryNumber,
		LocalDateTime deliveryDate,
		String productName,
		String productType,
		String variety,
		BigDecimal weight,
		BigDecimal unitPrice,
		BigDecimal totalPrice
) {}