package com.fruitwarehouse.product.service.impl;

import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.product.entity.ProductType;
import com.fruitwarehouse.common.exception.ProductNotFoundException;
import com.fruitwarehouse.product.repository.ProductRepository;
import com.fruitwarehouse.product.service.ProductEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductEntityServiceImpl implements ProductEntityService {

	private final ProductRepository productRepository;

	@Override
	public Product getById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException(id));
	}

	@Override
	public boolean existsById(Long id) {
		return productRepository.existsById(id);
	}

	@Override
	public List<Product> getAll() {
		return productRepository.findAll();
	}

	@Override
	public List<Product> getByProductType(ProductType.Code productType) {
		return productRepository.findByProductTypeCode(productType);
	}
}
