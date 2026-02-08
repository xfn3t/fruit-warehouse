package com.fruitwarehouse.delivery.service;

import com.fruitwarehouse.delivery.entity.DeliveryStatus;

public interface DeliveryStatusService {
	DeliveryStatus getByCode(DeliveryStatus.Code code);
}
