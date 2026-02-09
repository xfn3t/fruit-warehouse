package com.fruitwarehouse.delivery.service.impl;

import com.fruitwarehouse.delivery.controller.dto.request.CreateDeliveryRequest;
import com.fruitwarehouse.delivery.controller.dto.response.DeliveryItemResponse;
import com.fruitwarehouse.delivery.controller.dto.response.DeliveryResponse;
import com.fruitwarehouse.delivery.mapper.DeliveryApiMapper;
import com.fruitwarehouse.delivery.entity.Delivery;
import com.fruitwarehouse.delivery.entity.DeliveryItem;
import com.fruitwarehouse.delivery.entity.DeliveryStatus;
import com.fruitwarehouse.delivery.service.DeliveryEntityService;
import com.fruitwarehouse.delivery.service.DeliveryStatusService;
import com.fruitwarehouse.common.exception.ValidationException;
import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.supplier.entity.Supplier;
import com.fruitwarehouse.supplier.service.PriceEntityService;
import com.fruitwarehouse.product.service.ProductEntityService;
import com.fruitwarehouse.delivery.service.DeliveryService;
import com.fruitwarehouse.supplier.service.SupplierEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

	private final DeliveryEntityService deliveryEntityService;
	private final SupplierEntityService supplierEntityService;
	private final ProductEntityService productEntityService;
	private final DeliveryStatusService deliveryStatusService;
	private final PriceEntityService priceEntityService;
	private final DeliveryApiMapper deliveryApiMapper;

	@Override
	public DeliveryResponse createDelivery(CreateDeliveryRequest request) {
		log.info("Creating delivery for supplier ID: {}", request.supplierId());

		Supplier supplier = supplierEntityService.getById(request.supplierId());

		Delivery delivery = new Delivery();
		delivery.setSupplier(supplier);
		delivery.setDeliveryDate(
				request.deliveryDate() != null ? request.deliveryDate() : LocalDateTime.now()
		);

		DeliveryStatus createdStatus = deliveryStatusService.getByCode(DeliveryStatus.Code.CREATED);
		delivery.setStatus(createdStatus);

		List<DeliveryItem> deliveryItems = new ArrayList<>();
		BigDecimal totalWeight = BigDecimal.ZERO;
		BigDecimal totalCost = BigDecimal.ZERO;

		for (var itemRequest : request.items()) {
			Product product = productEntityService.getById(itemRequest.productId());

			BigDecimal unitPrice = getUnitPrice(
					supplier.getId(),
					product.getId(),
					delivery.getDeliveryDate().toLocalDate()
			);

			DeliveryItem deliveryItem = new DeliveryItem();
			deliveryItem.setDelivery(delivery);
			deliveryItem.setProduct(product);
			deliveryItem.setWeight(itemRequest.weight());
			deliveryItem.setUnitPrice(unitPrice);
			deliveryItem.calculateTotalPrice();

			deliveryItems.add(deliveryItem);

			totalWeight = totalWeight.add(deliveryItem.getWeight());
			totalCost = totalCost.add(deliveryItem.getTotalPrice());
		}

		delivery.setItems(deliveryItems);
		Delivery savedDelivery = deliveryEntityService.save(delivery);

		log.info("Delivery created with ID: {}", savedDelivery.getId());

		return buildDeliveryResponse(savedDelivery, totalWeight, totalCost);
	}

	@Override
	@Transactional(readOnly = true)
	public DeliveryResponse getDelivery(Long id) {
		log.info("Getting delivery with ID: {}", id);

		Delivery delivery = deliveryEntityService.getByIdWithItems(id);

		BigDecimal totalWeight = calculateTotalWeight(delivery);
		BigDecimal totalCost = calculateTotalCost(delivery);

		return buildDeliveryResponse(delivery, totalWeight, totalCost);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DeliveryResponse> getAllDeliveries() {
		log.info("Getting all deliveries");

		List<Delivery> deliveries = deliveryEntityService.getAll();

		return deliveries.stream()
				.map(delivery -> {
					BigDecimal totalWeight = calculateTotalWeight(delivery);
					BigDecimal totalCost = calculateTotalCost(delivery);
					return buildDeliveryResponse(delivery, totalWeight, totalCost);
				})
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<DeliveryResponse> getDeliveriesBySupplier(Long supplierId) {
		log.info("Getting deliveries for supplier ID: {}", supplierId);

		if (!supplierEntityService.existsById(supplierId)) {
			throw new ValidationException("Supplier with ID " + supplierId + " not found");
		}

		List<Delivery> deliveries = deliveryEntityService.getBySupplierIdWithDetails(supplierId);

		return deliveries.stream()
				.map(delivery -> {
					BigDecimal totalWeight = calculateTotalWeight(delivery);
					BigDecimal totalCost = calculateTotalCost(delivery);
					return buildDeliveryResponse(delivery, totalWeight, totalCost);
				})
				.toList();
	}

	private BigDecimal getUnitPrice(Long supplierId, Long productId, LocalDate date) {
		return priceEntityService.getActivePrice(supplierId, productId, date)
				.orElseThrow(() -> new ValidationException(
						String.format("No active price found for supplier %d, product %d on date %s. " +
										"Please set price in supplier price list before creating delivery.",
								supplierId, productId, date)
				));
	}

	private BigDecimal calculateTotalWeight(Delivery delivery) {
		if (delivery.getItems() == null) {
			return BigDecimal.ZERO;
		}
		return delivery.getItems().stream()
				.map(DeliveryItem::getWeight)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal calculateTotalCost(Delivery delivery) {
		if (delivery.getItems() == null) {
			return BigDecimal.ZERO;
		}
		return delivery.getItems().stream()
				.map(DeliveryItem::getTotalPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private DeliveryResponse buildDeliveryResponse(Delivery delivery, BigDecimal totalWeight, BigDecimal totalCost) {
		DeliveryResponse response = deliveryApiMapper.toDeliveryResponse(delivery);
		List<DeliveryItemResponse> itemResponses = deliveryApiMapper.toDeliveryItemResponseList(delivery.getItems());

		return DeliveryResponse.builder()
				.id(response.id())
				.deliveryNumber(response.deliveryNumber())
				.supplierId(response.supplierId())
				.supplierName(response.supplierName())
				.deliveryDate(response.deliveryDate())
				.status(response.status())
				.createdAt(response.createdAt())
				.items(itemResponses)
				.totalWeight(totalWeight)
				.totalCost(totalCost)
				.build();
	}
}
