package com.fruitwarehouse.common.exception;

import lombok.Getter;

@Getter
public abstract class EntityNotFoundException extends RuntimeException {
	private final String entityName;

	public EntityNotFoundException(String entityName, Long id) {
		super(String.format("%s with id %d not found", entityName, id));
		this.entityName = entityName;
	}

	public EntityNotFoundException(String entityName, String message) {
		super(message);
		this.entityName = entityName;
	}
}
