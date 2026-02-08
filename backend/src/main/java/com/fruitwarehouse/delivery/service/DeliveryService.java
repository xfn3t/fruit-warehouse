package com.fruitwarehouse.delivery.service;

import com.fruitwarehouse.delivery.controller.dto.request.CreateDeliveryRequest;
import com.fruitwarehouse.delivery.controller.dto.response.DeliveryResponse;

import java.util.List;

public interface DeliveryService {
	DeliveryResponse createDelivery(CreateDeliveryRequest request);
	DeliveryResponse getDelivery(Long id);
	List<DeliveryResponse> getAllDeliveries();
	List<DeliveryResponse> getDeliveriesBySupplier(Long supplierId);
}
