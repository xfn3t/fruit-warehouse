package com.fruitwarehouse.common.exception;

public class DeliveryNotFoundException extends EntityNotFoundException {
	public DeliveryNotFoundException(Long id) {
		super("Delivery", id);
	}

	public DeliveryNotFoundException(String message) {
		super("Delivery", message);
	}
}
