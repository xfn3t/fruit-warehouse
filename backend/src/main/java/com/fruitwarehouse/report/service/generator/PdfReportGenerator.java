package com.fruitwarehouse.report.service.generator;

import com.fruitwarehouse.report.controller.dto.response.DetailedReportItemResponse;
import com.fruitwarehouse.report.controller.dto.response.ReportItemResponse;
import com.fruitwarehouse.report.controller.dto.response.ReportResponse;
import com.fruitwarehouse.report.service.ReportGenerator;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component("pdfReportGenerator")
public class PdfReportGenerator implements ReportGenerator {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	private static final String DEFAULT_FONT_CLASSPATH = "fonts/DejaVuSans.ttf";

	@Override
	public ResponseEntity<?> generate(ReportResponse reportResponse) {
		try {
			byte[] pdfBytes = generatePdfBytes(reportResponse);

			String filename = String.format("delivery_report_%s_%s.pdf",
					reportResponse.startDate(), reportResponse.endDate());

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.contentType(MediaType.APPLICATION_PDF)
					.body(pdfBytes);

		} catch (Exception e) {
			log.error("Error generating PDF report", e);
			throw new RuntimeException("Failed to generate PDF report", e);
		}
	}

	private byte[] generatePdfBytes(ReportResponse reportResponse) throws Exception {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdf = new PdfDocument(writer);

			PageSize pageSize = reportResponse.detailed()
					? PageSize.A4.rotate()
					: PageSize.A4;

			Document document = new Document(pdf, pageSize);

			PdfFont font = loadFontOrThrow();
			document.setFont(font);

			document.add(new Paragraph("Отчет по доставкам")
					.setFontSize(16)
					.setBold()
					.setTextAlignment(TextAlignment.CENTER));

			document.add(new Paragraph(String.format("Период: %s - %s",
					reportResponse.startDate().format(DATE_FORMATTER),
					reportResponse.endDate().format(DATE_FORMATTER)))
					.setFontSize(12)
					.setTextAlignment(TextAlignment.CENTER)
					.setMarginBottom(20));

			if (reportResponse.detailed()) {
				generateDetailedReport(document, reportResponse, font);
			} else {
				generateSummaryReport(document, reportResponse, font);
			}

			String totalWeightStr = reportResponse.totalWeight() == null
					? "0.00"
					: reportResponse.totalWeight().setScale(2, RoundingMode.HALF_UP).toPlainString();

			String totalCostStr = reportResponse.totalCost() == null
					? "0.00"
					: reportResponse.totalCost().setScale(2, RoundingMode.HALF_UP).toPlainString();

			document.add(new Paragraph(String.format("Общий вес: %s кг", totalWeightStr))
					.setFontSize(12)
					.setBold()
					.setMarginTop(10));

			document.add(new Paragraph(String.format("Общая стоимость: %s руб.", totalCostStr))
					.setFontSize(12)
					.setBold()
					.setMarginBottom(20));

			document.close();
			return baos.toByteArray();
		}
	}

	private PdfFont loadFontOrThrow() {

		try (InputStream fontStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(PdfReportGenerator.DEFAULT_FONT_CLASSPATH)) {

			if (fontStream == null) {
				String msg = "Font file not found in classpath: " + PdfReportGenerator.DEFAULT_FONT_CLASSPATH +
						". Поместите TTF-файл в src/main/resources/" + PdfReportGenerator.DEFAULT_FONT_CLASSPATH;
				log.error(msg);
				throw new RuntimeException(msg);
			}

			byte[] fontBytes = fontStream.readAllBytes();
			return PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
		} catch (Exception e) {
			log.error("Failed to load font from classpath {}", PdfReportGenerator.DEFAULT_FONT_CLASSPATH, e);

			try {
				return PdfFontFactory.createFont(StandardFonts.COURIER);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to load fallback font", ex);
			}
		}
	}

	private void generateSummaryReport(Document document, ReportResponse reportResponse, PdfFont font) {
		List<ReportItemResponse> items = reportResponse.summaryItems();

		if (items == null || items.isEmpty()) {
			document.add(new Paragraph("Нет данных за указанный период")
					.setFontSize(12)
					.setItalic());
			return;
		}

		Table table = new Table(UnitValue.createPercentArray(new float[]{30, 22, 18, 15, 15}));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setMarginBottom(20);

		addTableHeader(table, "Поставщик", font);
		addTableHeader(table, "Тип продукта", font);
		addTableHeader(table, "Сорт", font);
		addTableHeader(table, "Вес (кг)", font);
		addTableHeader(table, "Стоимость (руб)", font);

		for (ReportItemResponse item : items) {
			Cell c1 = new Cell().add(new Paragraph(nullToEmpty(item.supplierName())).setFont(font)).setPadding(4f);
			Cell c2 = new Cell().add(new Paragraph(nullToEmpty(item.productType())).setFont(font)).setPadding(4f);
			Cell c3 = new Cell().add(new Paragraph(nullToEmpty(item.variety())).setFont(font)).setPadding(4f);

			String weight = item.totalWeight() == null
					? "0.00"
					: item.totalWeight().setScale(2, RoundingMode.HALF_UP).toPlainString();

			String cost = item.totalCost() == null
					? "0.00"
					: item.totalCost().setScale(2, RoundingMode.HALF_UP).toPlainString();

			Cell c4 = new Cell().add(new Paragraph(weight).setFont(font)).setPadding(4f).setTextAlignment(TextAlignment.RIGHT);
			Cell c5 = new Cell().add(new Paragraph(cost).setFont(font)).setPadding(4f).setTextAlignment(TextAlignment.RIGHT);

			table.addCell(c1);
			table.addCell(c2);
			table.addCell(c3);
			table.addCell(c4);
			table.addCell(c5);
		}

		document.add(table);
	}

	private void generateDetailedReport(Document document, ReportResponse reportResponse, PdfFont font) {
		List<DetailedReportItemResponse> items = reportResponse.detailedItems();

		if (items == null || items.isEmpty()) {
			document.add(new Paragraph("Нет данных за указанный период")
					.setFontSize(12)
					.setItalic());
			return;
		}

		Table table = new Table(UnitValue.createPercentArray(new float[]{22, 12, 14, 16, 10, 10, 8, 10, 12}));
		table.setWidth(UnitValue.createPercentValue(100));
		table.setMarginBottom(20);

		addTableHeader(table, "Поставщик", font);
		addTableHeader(table, "Номер доставки", font);
		addTableHeader(table, "Дата доставки", font);
		addTableHeader(table, "Продукт", font);
		addTableHeader(table, "Тип", font);
		addTableHeader(table, "Сорт", font);
		addTableHeader(table, "Вес (кг)", font);
		addTableHeader(table, "Цена за ед.", font);
		addTableHeader(table, "Стоимость", font);

		for (DetailedReportItemResponse item : items) {
			Cell c1 = new Cell().add(new Paragraph(nullToEmpty(item.supplierName())).setFont(font)).setPadding(4f);
			String deliveryNumber = safeShortUuid(item.deliveryNumber() != null ? item.deliveryNumber().toString() : null);
			Cell c2 = new Cell().add(new Paragraph(deliveryNumber).setFont(font)).setPadding(4f);
			String deliveryDateStr = item.deliveryDate() == null ? "" : item.deliveryDate().format(DATE_TIME_FORMATTER);
			Cell c3 = new Cell().add(new Paragraph(deliveryDateStr).setFont(font)).setPadding(4f);

			Cell c4 = new Cell().add(new Paragraph(nullToEmpty(item.productName())).setFont(font)).setPadding(4f);
			Cell c5 = new Cell().add(new Paragraph(nullToEmpty(item.productType())).setFont(font)).setPadding(4f);
			Cell c6 = new Cell().add(new Paragraph(nullToEmpty(item.variety())).setFont(font)).setPadding(4f);

			String weight = item.weight() == null ? "0.00" : item.weight().setScale(2, RoundingMode.HALF_UP).toPlainString();
			String unitPrice = item.unitPrice() == null ? "0.00" : item.unitPrice().setScale(2, RoundingMode.HALF_UP).toPlainString();
			String totalPrice = item.totalPrice() == null ? "0.00" : item.totalPrice().setScale(2, RoundingMode.HALF_UP).toPlainString();

			Cell c7 = new Cell().add(new Paragraph(weight).setFont(font)).setPadding(4f).setTextAlignment(TextAlignment.RIGHT);
			Cell c8 = new Cell().add(new Paragraph(unitPrice).setFont(font)).setPadding(4f).setTextAlignment(TextAlignment.RIGHT);
			Cell c9 = new Cell().add(new Paragraph(totalPrice).setFont(font)).setPadding(4f).setTextAlignment(TextAlignment.RIGHT);

			table.addCell(c1);
			table.addCell(c2);
			table.addCell(c3);
			table.addCell(c4);
			table.addCell(c5);
			table.addCell(c6);
			table.addCell(c7);
			table.addCell(c8);
			table.addCell(c9);
		}

		document.add(table);
	}

	private void addTableHeader(Table table, String header, PdfFont font) {
		Cell cell = new Cell();
		cell.add(new Paragraph(header).setFont(font).setBold().setTextAlignment(TextAlignment.CENTER));
		cell.setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(240, 240, 240));
		cell.setPadding(6f);
		table.addHeaderCell(cell);
	}

	private static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

	private static String safeShortUuid(String uuidStr) {
		if (uuidStr == null) return "";
		return uuidStr.length() <= 8 ? uuidStr : uuidStr.substring(0, 8);
	}
}
