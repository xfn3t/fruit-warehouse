package com.fruitwarehouse.supplier.service.impl;

import com.fruitwarehouse.supplier.controller.dto.request.CreatePriceRequest;
import com.fruitwarehouse.supplier.controller.dto.response.PriceResponse;
import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.supplier.entity.Supplier;
import com.fruitwarehouse.supplier.entity.SupplierProductPrice;
import com.fruitwarehouse.common.exception.ValidationException;
import com.fruitwarehouse.supplier.repository.SupplierProductPriceRepository;
import com.fruitwarehouse.supplier.service.SupplierPriceService;
import com.fruitwarehouse.product.service.ProductEntityService;
import com.fruitwarehouse.supplier.service.SupplierEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SupplierPriceServiceImpl implements SupplierPriceService {

	private final SupplierEntityService supplierEntityService;
	private final ProductEntityService productEntityService;
	private final SupplierProductPriceRepository priceRepository;

	@Override
	public PriceResponse addPrice(Long supplierId, CreatePriceRequest request) {
		log.info("Adding price for supplier ID: {}, product ID: {}", supplierId, request.productId());

		Supplier supplier = supplierEntityService.getById(supplierId);
		Product product = productEntityService.getById(request.productId());

		validatePricePeriod(supplierId, request.productId(),
				request.effectiveFrom(), request.effectiveTo());

		SupplierProductPrice price = new SupplierProductPrice();
		price.setSupplier(supplier);
		price.setProduct(product);
		price.setPrice(request.price());
		price.setEffectiveFrom(request.effectiveFrom());
		price.setEffectiveTo(request.effectiveTo());

		SupplierProductPrice savedPrice = priceRepository.save(price);

		log.info("Price created with ID: {}", savedPrice.getId());

		return buildPriceResponse(savedPrice);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceResponse> getSupplierPrices(Long supplierId, Long productId) {
		log.info("Getting prices for supplier ID: {}, product ID: {}", supplierId, productId);

		supplierEntityService.getById(supplierId);

		List<SupplierProductPrice> prices;
		if (productId != null) {
			productEntityService.getById(productId);
			prices = priceRepository.findBySupplierIdAndProductId(supplierId, productId);
		} else {
			prices = priceRepository.findBySupplierId(supplierId);
		}

		return prices.stream()
				.map(this::buildPriceResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PriceResponse> getActivePrices(Long supplierId) {
		log.info("Getting active prices for supplier ID: {}", supplierId);

		supplierEntityService.getById(supplierId);

		List<SupplierProductPrice> prices = priceRepository.findActiveBySupplierId(
				supplierId, LocalDate.now()
		);

		return prices.stream()
				.map(this::buildPriceResponse)
				.toList();
	}

	@Override
	public void deletePrice(Long supplierId, Long priceId) {
		log.info("Deleting price ID: {} for supplier ID: {}", priceId, supplierId);

		SupplierProductPrice price = priceRepository.findByIdAndSupplierId(priceId, supplierId)
				.orElseThrow(() -> new ValidationException(
						"Price not found or doesn't belong to this supplier"));

		priceRepository.delete(price);

		log.info("Price deleted successfully");
	}

	private void validatePricePeriod(Long supplierId, Long productId,
									 LocalDate effectiveFrom, LocalDate effectiveTo) {

		if (effectiveTo != null && effectiveFrom.isAfter(effectiveTo)) {
			throw new ValidationException("Effective from date cannot be after effective to date");
		}

		List<SupplierProductPrice> existingPrices = priceRepository
				.findBySupplierIdAndProductId(supplierId, productId);

		for (SupplierProductPrice existing : existingPrices) {
			if (isPeriodOverlapping(existing.getEffectiveFrom(), existing.getEffectiveTo(),
					effectiveFrom, effectiveTo)) {
				throw new ValidationException(
						String.format("Price period overlaps with existing price for period %s to %s",
								existing.getEffectiveFrom(),
								existing.getEffectiveTo() != null ?
										existing.getEffectiveTo() : "indefinite")
				);
			}
		}
	}

	private boolean isPeriodOverlapping(LocalDate start1, LocalDate end1,
										LocalDate start2, LocalDate end2) {
		// 1. Первый период заканчивается до начала второго (если end1 не null)
		// 2. Второй период заканчивается до начала первого (если end2 не null)
		return !((end1 != null && end1.isBefore(start2)) ||
				(end2 != null && end2.isBefore(start1)));
	}

	private PriceResponse buildPriceResponse(SupplierProductPrice price) {
		return PriceResponse.builder()
				.id(price.getId())
				.supplierId(price.getSupplier().getId())
				.productId(price.getProduct().getId())
				.productName(price.getProduct().getName())
				.productType(price.getProduct().getProductType().getName())
				.variety(price.getProduct().getVarietyName())
				.price(price.getPrice())
				.effectiveFrom(price.getEffectiveFrom())
				.effectiveTo(price.getEffectiveTo())
				.createdAt(price.getCreatedAt())
				.build();
	}
}