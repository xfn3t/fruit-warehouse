package com.fruitwarehouse.common.exception;

public class ProductNotFoundException extends EntityNotFoundException {
	public ProductNotFoundException(Long id) {
		super("Product", id);
	}

	public ProductNotFoundException(String message) {
		super("Product", message);
	}
}
