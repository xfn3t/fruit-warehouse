package com.fruitwarehouse.report.repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DetailedReportItemDto {
	private String supplierName;
	private UUID deliveryNumber;
	private LocalDateTime deliveryDate;
	private String productName;
	private String productType;
	private String variety;
	private BigDecimal weight;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
}
