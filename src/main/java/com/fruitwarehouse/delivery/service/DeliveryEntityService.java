package com.fruitwarehouse.delivery.service;

import com.fruitwarehouse.delivery.entity.Delivery;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryEntityService {
	Delivery getById(Long id);
	Delivery getByIdWithItems(Long id);
	Delivery save(Delivery delivery);
	List<Delivery> getBySupplierId(Long supplierId);
	List<Delivery> getBySupplierIdWithDetails(Long supplierId);
	List<Delivery> getByDeliveryDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
