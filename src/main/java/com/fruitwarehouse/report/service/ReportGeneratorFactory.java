package com.fruitwarehouse.report.service;

import com.fruitwarehouse.report.controller.dto.request.ReportFormat;
import com.fruitwarehouse.report.service.generator.JsonReportGenerator;
import com.fruitwarehouse.report.service.generator.PdfReportGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportGeneratorFactory {

	private final JsonReportGenerator jsonReportGenerator;
	private final PdfReportGenerator pdfReportGenerator;

	private Map<ReportFormat, ReportGenerator> generators;

	@PostConstruct
	public void init() {
		generators = new EnumMap<>(ReportFormat.class);
		generators.put(ReportFormat.JSON, jsonReportGenerator);
		generators.put(ReportFormat.PDF, pdfReportGenerator);
	}

	public ReportGenerator getGenerator(ReportFormat format) {
		ReportGenerator generator = generators.get(format);
		if (generator == null) {
			throw new IllegalArgumentException("Unsupported report format: " + format);
		}
		return generator;
	}
}