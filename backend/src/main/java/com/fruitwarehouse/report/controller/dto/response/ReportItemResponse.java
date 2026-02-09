package com.fruitwarehouse.report.controller.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReportItemResponse(
		String supplierName,
		String productType,
		String variety,
		BigDecimal totalWeight,
		BigDecimal totalCost
) {}