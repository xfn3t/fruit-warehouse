package com.fruitwarehouse.report.service.impl;

import com.fruitwarehouse.report.controller.dto.request.ReportRequest;
import com.fruitwarehouse.report.controller.dto.response.DetailedReportItemResponse;
import com.fruitwarehouse.report.controller.dto.response.ReportItemResponse;
import com.fruitwarehouse.report.controller.dto.response.ReportResponse;
import com.fruitwarehouse.report.mapper.ReportApiMapper;
import com.fruitwarehouse.common.exception.ValidationException;
import com.fruitwarehouse.report.repository.ReportRepository;
import com.fruitwarehouse.report.repository.dto.DetailedReportItemDto;
import com.fruitwarehouse.report.service.ReportGeneratorFactory;
import com.fruitwarehouse.report.service.ReportService;
import com.fruitwarehouse.supplier.repository.dto.ReportItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	private final ReportApiMapper reportApiMapper;
	private final ReportGeneratorFactory reportGeneratorFactory;

	@Override
	public ResponseEntity<?> generateReport(ReportRequest request) {
		log.info("Generating report from {} to {}, detailed: {}, format: {}",
				request.startDate(), request.endDate(), request.detailed(), request.format());

		validateReportRequest(request);

		LocalDateTime startDate = request.startDate().atStartOfDay();
		LocalDateTime endDate = request.endDate().atTime(LocalTime.MAX);

		List<ReportItemResponse> summaryItems = null;
		List<DetailedReportItemResponse> detailedItems = null;
		BigDecimal totalWeight;
		BigDecimal totalCost;

		if (request.detailed()) {
			List<DetailedReportItemDto> detailedData = reportRepository.findDetailedReportData(startDate, endDate);
			detailedItems = reportApiMapper.toDetailedReportItemResponseList(detailedData);

			totalWeight = detailedData.stream()
					.map(DetailedReportItemDto::getWeight)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			totalCost = detailedData.stream()
					.map(DetailedReportItemDto::getTotalPrice)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		} else {
			List<ReportItemDto> summaryData = reportRepository.findSummaryReportData(startDate, endDate);
			summaryItems = reportApiMapper.toReportItemResponseList(summaryData);

			totalWeight = summaryData.stream()
					.map(ReportItemDto::getTotalWeight)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			totalCost = summaryData.stream()
					.map(ReportItemDto::getTotalCost)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		ReportResponse reportResponse = ReportResponse.builder()
				.startDate(request.startDate())
				.endDate(request.endDate())
				.detailed(request.detailed())
				.summaryItems(summaryItems)
				.detailedItems(detailedItems)
				.totalWeight(totalWeight)
				.totalCost(totalCost)
				.build();

		log.info("Report generated with total weight: {}, total cost: {}", totalWeight, totalCost);

		var generator = reportGeneratorFactory.getGenerator(request.format());
		return generator.generate(reportResponse);
	}

	private void validateReportRequest(ReportRequest request) {
		if (request.startDate().isAfter(request.endDate())) {
			throw new ValidationException("Start date cannot be after end date");
		}

		if (request.startDate().isAfter(LocalDate.now())) {
			throw new ValidationException("Start date cannot be in the future");
		}

		if (request.startDate().plusYears(1).isBefore(request.endDate())) {
			throw new ValidationException("Report period cannot exceed 1 year");
		}
	}
}