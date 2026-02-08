package com.fruitwarehouse.report.controller;

import com.fruitwarehouse.report.controller.dto.request.ReportRequest;
import com.fruitwarehouse.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Report generation APIs")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@GetMapping
	@Operation(summary = "Generate delivery report")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Report generated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public ResponseEntity<?> generateReport(
			@Valid ReportRequest request) {
		return reportService.generateReport(request);
	}
}