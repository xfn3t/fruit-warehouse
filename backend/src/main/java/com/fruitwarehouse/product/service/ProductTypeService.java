package com.fruitwarehouse.product.service;

import com.fruitwarehouse.product.entity.ProductType;

public interface ProductTypeService {
	ProductType getByCode(ProductType.Code code);
}
