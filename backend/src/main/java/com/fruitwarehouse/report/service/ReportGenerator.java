package com.fruitwarehouse.report.service;

import com.fruitwarehouse.report.controller.dto.response.ReportResponse;
import org.springframework.http.ResponseEntity;

public interface ReportGenerator {
	ResponseEntity<?> generate(ReportResponse reportResponse);
}