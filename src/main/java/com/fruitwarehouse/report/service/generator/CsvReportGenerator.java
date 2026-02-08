package com.fruitwarehouse.report.service.generator;

import com.fruitwarehouse.report.controller.dto.response.DetailedReportItemResponse;
import com.fruitwarehouse.report.controller.dto.response.ReportItemResponse;
import com.fruitwarehouse.report.controller.dto.response.ReportResponse;
import com.fruitwarehouse.report.service.ReportGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component("csvReportGenerator")
public class CsvReportGenerator implements ReportGenerator {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	// UTF-8 BOM to help Excel correctly detect UTF-8 and display Cyrillic
	private static final byte[] UTF8_BOM = new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF};

	@Override
	public ResponseEntity<?> generate(ReportResponse reportResponse) {
		try {
			byte[] csvBytes = buildCsv(reportResponse);
			String filename = String.format("delivery_report_%s_%s.csv",
					reportResponse.startDate(), reportResponse.endDate());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
			headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

			return ResponseEntity.ok()
					.headers(headers)
					.body(csvBytes);
		} catch (Exception e) {
			log.error("Error generating CSV report", e);
			throw new RuntimeException("Failed to generate CSV report", e);
		}
	}

	private byte[] buildCsv(ReportResponse reportResponse) {
		StringBuilder sb = new StringBuilder();

		// BOM
		sb.append(new String(UTF8_BOM, StandardCharsets.UTF_8));

		// Header info
		sb.append(quote("Report period")).append(",");
		sb.append(quote(String.format("%s - %s",
				safeFormatDate(reportResponse.startDate()),
				safeFormatDate(reportResponse.endDate())))).append("\n");

		sb.append(quote("Detailed")).append(",");
		sb.append(quote(Boolean.toString(reportResponse.detailed()))).append("\n\n");

		if (reportResponse.detailed()) {
			appendDetailedCsv(sb, reportResponse.detailedItems());
		} else {
			appendSummaryCsv(sb, reportResponse.summaryItems());
		}

		// Totals
		sb.append("\n");
		sb.append(quote("Total weight")).append(",");
		sb.append(quote(reportResponse.totalWeight() == null ? "0.00" :
				reportResponse.totalWeight().setScale(2).toPlainString())).append("\n");

		sb.append(quote("Total cost")).append(",");
		sb.append(quote(reportResponse.totalCost() == null ? "0.00" :
				reportResponse.totalCost().setScale(2).toPlainString())).append("\n");

		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	private void appendSummaryCsv(StringBuilder sb, List<ReportItemResponse> items) {
		// Header
		sb.append(quote("Supplier")).append(",");
		sb.append(quote("Product Type")).append(",");
		sb.append(quote("Variety")).append(",");
		sb.append(quote("Total Weight (kg)")).append(",");
		sb.append(quote("Total Cost")).append("\n");

		if (items == null || items.isEmpty()) {
			sb.append(quote("No data")).append("\n");
			return;
		}

		for (ReportItemResponse it : items) {
			sb.append(quote(safe(it.supplierName()))).append(",");
			sb.append(quote(safe(it.productType()))).append(",");
			sb.append(quote(safe(it.variety()))).append(",");

			String weight = it.totalWeight() == null ? "0.00" : it.totalWeight().setScale(2).toPlainString();
			String cost = it.totalCost() == null ? "0.00" : it.totalCost().setScale(2).toPlainString();

			sb.append(quote(weight)).append(",");
			sb.append(quote(cost)).append("\n");
		}
	}

	private void appendDetailedCsv(StringBuilder sb, List<DetailedReportItemResponse> items) {
		// Header
		sb.append(quote("Supplier")).append(",");
		sb.append(quote("Delivery Number")).append(",");
		sb.append(quote("Delivery Date")).append(",");
		sb.append(quote("Product Name")).append(",");
		sb.append(quote("Product Type")).append(",");
		sb.append(quote("Variety")).append(",");
		sb.append(quote("Weight (kg)")).append(",");
		sb.append(quote("Unit Price")).append(",");
		sb.append(quote("Total Price")).append("\n");

		if (items == null || items.isEmpty()) {
			sb.append(quote("No data")).append("\n");
			return;
		}

		for (DetailedReportItemResponse it : items) {
			sb.append(quote(safe(it.supplierName()))).append(",");
			sb.append(quote(safeShortUuid(it.deliveryNumber() == null ? null : it.deliveryNumber().toString()))).append(",");
			sb.append(quote(it.deliveryDate() == null ? "" : it.deliveryDate().format(DATE_TIME_FORMATTER))).append(",");
			sb.append(quote(safe(it.productName()))).append(",");
			sb.append(quote(safe(it.productType()))).append(",");
			sb.append(quote(safe(it.variety()))).append(",");

			String weight = it.weight() == null ? "0.00" : it.weight().setScale(2).toPlainString();
			String unitPrice = it.unitPrice() == null ? "0.00" : it.unitPrice().setScale(2).toPlainString();
			String totalPrice = it.totalPrice() == null ? "0.00" : it.totalPrice().setScale(2).toPlainString();

			sb.append(quote(weight)).append(",");
			sb.append(quote(unitPrice)).append(",");
			sb.append(quote(totalPrice)).append("\n");
		}
	}

	private String quote(String field) {
		if (field == null) return "\"\"";
		boolean mustQuote = field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r");
		String escaped = field.replace("\"", "\"\""); // double quotes
		return mustQuote ? "\"" + escaped + "\"" : escaped;
	}

	private String safe(String s) {
		return s == null ? "" : s;
	}

	private String safeShortUuid(String uuid) {
		if (uuid == null) return "";
		return uuid.length() <= 8 ? uuid : uuid.substring(0, 8);
	}

	private String safeFormatDate(java.time.LocalDate date) {
		return date == null ? "" : date.format(DATE_FORMATTER);
	}
}
