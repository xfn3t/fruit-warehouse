package com.fruitwarehouse.report.controller.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record ReportResponse(
		LocalDate startDate,
		LocalDate endDate,
		boolean detailed,
		List<ReportItemResponse> summaryItems,
		List<DetailedReportItemResponse> detailedItems,
		BigDecimal totalWeight,
		BigDecimal totalCost
) {}