package com.fruitwarehouse.product.service;

import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.product.entity.ProductType;

import java.util.List;

public interface ProductEntityService {
	Product getById(Long id);
	boolean existsById(Long id);
	List<Product> getAll();
	List<Product> getByProductType(ProductType.Code productType);
}
