package com.fruitwarehouse.delivery.service.impl;

import com.fruitwarehouse.delivery.entity.DeliveryStatus;
import com.fruitwarehouse.common.exception.ValidationException;
import com.fruitwarehouse.delivery.repository.DeliveryStatusRepository;
import com.fruitwarehouse.delivery.service.DeliveryStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryStatusServiceImpl implements DeliveryStatusService {

	private final DeliveryStatusRepository deliveryStatusRepository;

	@Override
	public DeliveryStatus getByCode(DeliveryStatus.Code code) {
		return deliveryStatusRepository.findByCode(code)
				.orElseThrow(() -> new ValidationException(
						String.format("Delivery status with code %s not found", code)
				));
	}
}