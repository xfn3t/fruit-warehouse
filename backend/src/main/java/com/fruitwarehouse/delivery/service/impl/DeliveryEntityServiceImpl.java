package com.fruitwarehouse.delivery.service.impl;

import com.fruitwarehouse.delivery.entity.Delivery;
import com.fruitwarehouse.common.exception.DeliveryNotFoundException;
import com.fruitwarehouse.delivery.repository.DeliveryRepository;
import com.fruitwarehouse.delivery.service.DeliveryEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryEntityServiceImpl implements DeliveryEntityService {

	private final DeliveryRepository deliveryRepository;

	@Override
	public Delivery getById(Long id) {
		return deliveryRepository.findById(id)
				.orElseThrow(() -> new DeliveryNotFoundException(id));
	}

	@Override
	public Delivery getByIdWithItems(Long id) {
		return deliveryRepository.findByIdWithItems(id)
				.orElseThrow(() -> new DeliveryNotFoundException(id));
	}

	@Override
	@Transactional
	public Delivery save(Delivery delivery) {
		return deliveryRepository.save(delivery);
	}

	@Override
	public List<Delivery> getBySupplierId(Long supplierId) {
		return deliveryRepository.findBySupplierId(supplierId);
	}

	@Override
	public List<Delivery> getAll() {
		return deliveryRepository.findAll();
	}

	@Override
	public List<Delivery> getBySupplierIdWithDetails(Long supplierId) {
		return deliveryRepository.findBySupplierIdWithDetails(supplierId);
	}

	@Override
	public List<Delivery> getByDeliveryDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
		return deliveryRepository.findByDeliveryDateBetweenWithDetails(startDate, endDate);
	}
}
