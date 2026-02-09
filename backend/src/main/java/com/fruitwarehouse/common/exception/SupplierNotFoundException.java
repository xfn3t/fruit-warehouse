package com.fruitwarehouse.common.exception;

public class SupplierNotFoundException extends EntityNotFoundException {
	public SupplierNotFoundException(Long id) {
		super("Supplier", id);
	}

	public SupplierNotFoundException(String message) {
		super("Supplier", message);
	}
}
