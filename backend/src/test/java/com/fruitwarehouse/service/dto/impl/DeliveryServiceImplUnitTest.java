package com.fruitwarehouse.service.dto.impl;

import com.fruitwarehouse.delivery.controller.dto.request.CreateDeliveryRequest;
import com.fruitwarehouse.delivery.controller.dto.request.DeliveryItemRequest;
import com.fruitwarehouse.delivery.controller.dto.response.DeliveryResponse;
import com.fruitwarehouse.delivery.mapper.DeliveryApiMapper;
import com.fruitwarehouse.delivery.entity.Delivery;
import com.fruitwarehouse.delivery.entity.DeliveryStatus;
import com.fruitwarehouse.delivery.service.DeliveryEntityService;
import com.fruitwarehouse.delivery.service.DeliveryStatusService;
import com.fruitwarehouse.delivery.service.impl.DeliveryServiceImpl;
import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.supplier.entity.Supplier;
import com.fruitwarehouse.supplier.service.PriceEntityService;
import com.fruitwarehouse.product.service.ProductEntityService;
import com.fruitwarehouse.supplier.service.SupplierEntityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceImplUnitTest {

	@Mock
	private DeliveryEntityService deliveryEntityService;
	@Mock
	private SupplierEntityService supplierEntityService;
	@Mock
	private ProductEntityService productEntityService;
	@Mock
	private DeliveryStatusService deliveryStatusService;
	@Mock
	private PriceEntityService priceEntityService;
	@Mock
	private DeliveryApiMapper deliveryApiMapper;

	@InjectMocks
	private DeliveryServiceImpl deliveryService;

	@Test
	void createDelivery_ShouldCalculateTotalPriceCorrectly() {

		Long supplierId = 1L;
		Long productId = 100L;
		BigDecimal weight = new BigDecimal("10.5");
		BigDecimal unitPrice = new BigDecimal("2.5");
		BigDecimal expectedTotalPrice = new BigDecimal("26.25");

		Supplier mockSupplier = new Supplier();
		mockSupplier.setId(supplierId);
		mockSupplier.setName("Test Supplier");

		Product mockProduct = new Product();
		mockProduct.setId(productId);
		mockProduct.setName("Golden Apple");

		DeliveryStatus mockStatus = new DeliveryStatus();
		mockStatus.setCode(DeliveryStatus.Code.CREATED);
		mockStatus.setName("Created");

		DeliveryItemRequest itemRequest = new DeliveryItemRequest(productId, weight);
		CreateDeliveryRequest request = new CreateDeliveryRequest(
				supplierId,
				LocalDateTime.now(),
				List.of(itemRequest)
		);

		Delivery savedDelivery = new Delivery();
		savedDelivery.setId(1L);

		when(supplierEntityService.getById(supplierId)).thenReturn(mockSupplier);
		when(productEntityService.getById(productId)).thenReturn(mockProduct);
		when(deliveryStatusService.getByCode(DeliveryStatus.Code.CREATED))
				.thenReturn(mockStatus); // Мокаем получение статуса
		when(priceEntityService.getActivePrice(any(), any(), any()))
				.thenReturn(java.util.Optional.of(unitPrice));
		when(deliveryEntityService.save(any(Delivery.class))).thenReturn(savedDelivery);
		when(deliveryApiMapper.toDeliveryResponse(any())).thenReturn(
				DeliveryResponse.builder()
						.id(1L)
						.supplierId(supplierId)
						.supplierName("Test Supplier")
						.build()
		);

		DeliveryResponse response = deliveryService.createDelivery(request);

		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.supplierName()).isEqualTo("Test Supplier");

		verify(supplierEntityService).getById(supplierId);
		verify(productEntityService).getById(productId);
		verify(deliveryStatusService).getByCode(DeliveryStatus.Code.CREATED);
		verify(priceEntityService).getActivePrice(eq(supplierId), eq(productId), any());
		verify(deliveryEntityService).save(any(Delivery.class));
	}
}