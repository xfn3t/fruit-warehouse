package com.fruitwarehouse.common.exception;

public class PriceConflictException extends ValidationException {
	public PriceConflictException(String message) {
		super(message);
	}

	public PriceConflictException(String message, Throwable cause) {
		super(message, cause);
	}
}