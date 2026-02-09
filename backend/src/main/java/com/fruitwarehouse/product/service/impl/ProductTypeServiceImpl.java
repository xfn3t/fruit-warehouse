package com.fruitwarehouse.product.service.impl;

import com.fruitwarehouse.product.entity.ProductType;
import com.fruitwarehouse.common.exception.ValidationException;
import com.fruitwarehouse.product.repository.ProductTypeRepository;
import com.fruitwarehouse.product.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService {

	private final ProductTypeRepository productTypeRepository;

	@Override
	public ProductType getByCode(ProductType.Code code) {
		return productTypeRepository.findByCode(code)
				.orElseThrow(() -> new ValidationException(
						String.format("Product type with code %s not found", code)
				));
	}
}