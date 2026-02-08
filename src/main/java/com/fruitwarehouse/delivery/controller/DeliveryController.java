package com.fruitwarehouse.delivery.controller;

import com.fruitwarehouse.delivery.controller.dto.request.CreateDeliveryRequest;
import com.fruitwarehouse.delivery.controller.dto.response.DeliveryResponse;
import com.fruitwarehouse.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deliveries")
@Tag(name = "Deliveries", description = "Delivery management APIs")
@RequiredArgsConstructor
public class DeliveryController {

	private final DeliveryService deliveryService;

	@PostMapping
	@Operation(summary = "Create a new delivery")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Delivery created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input"),
			@ApiResponse(responseCode = "404", description = "Supplier or product not found")
	})
	public ResponseEntity<DeliveryResponse> createDelivery(
			@Valid @RequestBody CreateDeliveryRequest request) {
		DeliveryResponse response = deliveryService.createDelivery(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get delivery by ID")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Delivery found"),
			@ApiResponse(responseCode = "404", description = "Delivery not found")
	})
	public ResponseEntity<DeliveryResponse> getDelivery(
			@PathVariable @Parameter(description = "Delivery ID") Long id) {
		DeliveryResponse response = deliveryService.getDelivery(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@Operation(summary = "Get all deliveries (last 30 days)")
	@ApiResponse(responseCode = "200", description = "List of deliveries retrieved")
	public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
		List<DeliveryResponse> responses = deliveryService.getAllDeliveries();
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/supplier/{supplierId}")
	@Operation(summary = "Get deliveries by supplier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "List of deliveries retrieved"),
			@ApiResponse(responseCode = "404", description = "Supplier not found")
	})
	public ResponseEntity<List<DeliveryResponse>> getDeliveriesBySupplier(
			@PathVariable @Parameter(description = "Supplier ID") Long supplierId) {
		List<DeliveryResponse> responses = deliveryService.getDeliveriesBySupplier(supplierId);
		return ResponseEntity.ok(responses);
	}
}
