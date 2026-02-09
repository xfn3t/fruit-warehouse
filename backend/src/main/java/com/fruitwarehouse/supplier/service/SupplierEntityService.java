package com.fruitwarehouse.supplier.service;

import com.fruitwarehouse.supplier.entity.Supplier;

public interface SupplierEntityService {
	Supplier getById(Long id);
	boolean existsById(Long id);
	Supplier save(Supplier supplier);
}
