package com.fruitwarehouse.supplier.service;

import com.fruitwarehouse.supplier.controller.dto.request.CreatePriceRequest;
import com.fruitwarehouse.supplier.controller.dto.response.PriceResponse;

import java.util.List;

public interface SupplierPriceService {
	PriceResponse addPrice(Long supplierId, CreatePriceRequest request);
	List<PriceResponse> getSupplierPrices(Long supplierId, Long productId);
	List<PriceResponse> getActivePrices(Long supplierId);
	void deletePrice(Long supplierId, Long priceId);
}