package com.fruitwarehouse.supplier.service;

import com.fruitwarehouse.supplier.entity.SupplierProductPrice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceEntityService {
	Optional<BigDecimal> getActivePrice(Long supplierId, Long productId, LocalDate date);
	Optional<BigDecimal> getCurrentPrice(Long supplierId, Long productId);
	List<SupplierProductPrice> getActivePrices(Long supplierId, LocalDate date);
}