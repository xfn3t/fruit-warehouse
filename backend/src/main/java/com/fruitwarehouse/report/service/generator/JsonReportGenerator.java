package com.fruitwarehouse.report.service.generator;

import com.fruitwarehouse.report.controller.dto.response.ReportResponse;
import com.fruitwarehouse.report.service.ReportGenerator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("jsonReportGenerator")
public class JsonReportGenerator implements ReportGenerator {

	@Override
	public ResponseEntity<?> generate(ReportResponse reportResponse) {
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(reportResponse);
	}
}