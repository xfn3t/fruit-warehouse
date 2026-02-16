package com.fruitwarehouse.report.service;

import com.fruitwarehouse.report.controller.dto.request.ReportRequest;
import org.springframework.http.ResponseEntity;

public interface ReportService {
	ResponseEntity<?> generateReport(ReportRequest request);
}