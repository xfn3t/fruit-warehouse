package com.fruitwarehouse.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
			EntityNotFoundException ex, WebRequest request) {
		log.warn("Entity not found: {}", ex.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
			ValidationException ex, WebRequest request) {
		log.warn("Validation error: {}", ex.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {
		log.warn("Method argument validation error");

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = error instanceof FieldError ?
					((FieldError) error).getField() : error.getObjectName();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message("Validation failed")
				.validationErrors(errors)
				.build();

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(
			ConstraintViolationException ex, WebRequest request) {
		log.warn("Constraint violation: {}", ex.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PriceConflictException.class)
	public ResponseEntity<ErrorResponse> handlePriceConflictException(
			PriceConflictException ex, WebRequest request) {
		log.warn("Price conflict: {}", ex.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase())
				.message(ex.getMessage())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(
			Exception ex, WebRequest request) {
		log.error("Unexpected error: ", ex);

		ErrorResponse errorResponse = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.message("An unexpected error occurred")
				.path(request.getDescription(false).replace("uri=", ""))
				.build();

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@lombok.Builder
	public record ErrorResponse(
			LocalDateTime timestamp,
			int status,
			String error,
			String message,
			String path
	) {}

	@lombok.Builder
	public record ValidationErrorResponse(
			LocalDateTime timestamp,
			int status,
			String error,
			String message,
			Map<String, String> validationErrors
	) {}
}
