package com.fruitwarehouse.supplier.controller;

import com.fruitwarehouse.supplier.controller.dto.request.CreatePriceRequest;
import com.fruitwarehouse.supplier.controller.dto.response.PriceResponse;
import com.fruitwarehouse.supplier.service.SupplierPriceService;
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
@RequestMapping("/api/v1/suppliers/{supplierId}/prices")
@Tag(name = "Supplier Prices", description = "Supplier price management APIs")
@RequiredArgsConstructor
public class SupplierPriceController {

	private final SupplierPriceService supplierPriceService;

	@PostMapping
	@Operation(summary = "Add or update price for supplier product")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Price created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input or overlapping period"),
			@ApiResponse(responseCode = "404", description = "Supplier or product not found")
	})
	public ResponseEntity<PriceResponse> addPrice(
			@PathVariable @Parameter(description = "Supplier ID") Long supplierId,
			@Valid @RequestBody CreatePriceRequest request) {
		PriceResponse response = supplierPriceService.addPrice(supplierId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@Operation(summary = "Get all prices for supplier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "List of prices retrieved"),
			@ApiResponse(responseCode = "404", description = "Supplier not found")
	})
	public ResponseEntity<List<PriceResponse>> getSupplierPrices(
			@PathVariable @Parameter(description = "Supplier ID") Long supplierId,
			@RequestParam(required = false) Long productId) {
		List<PriceResponse> responses = supplierPriceService.getSupplierPrices(supplierId, productId);
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/active")
	@Operation(summary = "Get active prices for supplier")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "List of active prices retrieved"),
			@ApiResponse(responseCode = "404", description = "Supplier not found")
	})
	public ResponseEntity<List<PriceResponse>> getActivePrices(
			@PathVariable @Parameter(description = "Supplier ID") Long supplierId) {
		List<PriceResponse> responses = supplierPriceService.getActivePrices(supplierId);
		return ResponseEntity.ok(responses);
	}

	@DeleteMapping("/{priceId}")
	@Operation(summary = "Delete price")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Price deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Price not found")
	})
	public ResponseEntity<Void> deletePrice(
			@PathVariable @Parameter(description = "Supplier ID") Long supplierId,
			@PathVariable @Parameter(description = "Price ID") Long priceId) {
		supplierPriceService.deletePrice(supplierId, priceId);
		return ResponseEntity.noContent().build();
	}
}