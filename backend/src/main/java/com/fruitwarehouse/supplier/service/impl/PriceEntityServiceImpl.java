package com.fruitwarehouse.supplier.service.impl;

import com.fruitwarehouse.supplier.entity.SupplierProductPrice;
import com.fruitwarehouse.supplier.repository.SupplierProductPriceRepository;
import com.fruitwarehouse.supplier.service.PriceEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceEntityServiceImpl implements PriceEntityService {

	private final SupplierProductPriceRepository supplierProductPriceRepository;

	@Override
	public Optional<BigDecimal> getActivePrice(Long supplierId, Long productId, LocalDate date) {
		return supplierProductPriceRepository.findActivePrice(supplierId, productId, date)
				.map(SupplierProductPrice::getPrice);
	}

	@Override
	public Optional<BigDecimal> getCurrentPrice(Long supplierId, Long productId) {
		return supplierProductPriceRepository.findCurrentPrice(supplierId, productId)
				.map(SupplierProductPrice::getPrice);
	}

	@Override
	public List<SupplierProductPrice> getActivePrices(Long supplierId, LocalDate date) {
		return supplierProductPriceRepository.findActiveBySupplierId(supplierId, date);
	}
}
