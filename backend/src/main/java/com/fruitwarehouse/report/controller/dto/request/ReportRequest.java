package com.fruitwarehouse.report.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReportRequest(
		@NotNull(message = "Start date is required")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate startDate,

		@NotNull(message = "End date is required")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate endDate,

		boolean detailed,

		ReportFormat format
) {
	public ReportRequest {
		if (format == null) {
			format = ReportFormat.JSON; // Значение по умолчанию
		}
	}
}