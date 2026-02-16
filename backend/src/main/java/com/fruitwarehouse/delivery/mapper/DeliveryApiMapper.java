package com.fruitwarehouse.delivery.mapper;

import com.fruitwarehouse.delivery.controller.dto.response.DeliveryItemResponse;
import com.fruitwarehouse.delivery.controller.dto.response.DeliveryResponse;
import com.fruitwarehouse.delivery.entity.Delivery;
import com.fruitwarehouse.delivery.entity.DeliveryItem;
import com.fruitwarehouse.delivery.entity.DeliveryStatus;
import com.fruitwarehouse.product.entity.ProductType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryApiMapper {

	@Mapping(target = "supplierId", source = "supplier.id")
	@Mapping(target = "supplierName", source = "supplier.name")
	@Mapping(target = "totalWeight", ignore = true)
	@Mapping(target = "totalCost", ignore = true)
	DeliveryResponse toDeliveryResponse(Delivery delivery);

	@Mapping(target = "productId", source = "product.id")
	@Mapping(target = "productName", source = "product.name")
	@Mapping(target = "productType", source = "product.productType")
	@Mapping(target = "variety", source = "product.varietyName")
	DeliveryItemResponse toDeliveryItemResponse(DeliveryItem item);

	default String map(DeliveryStatus value) {
		return value != null ? value.getName() : null;
	}

	default String map(ProductType value) {
		return value != null ? value.getName() : null;
	}

	List<DeliveryItemResponse> toDeliveryItemResponseList(List<DeliveryItem> items);
	List<DeliveryResponse> toDeliveryResponseList(List<Delivery> deliveries);
}
