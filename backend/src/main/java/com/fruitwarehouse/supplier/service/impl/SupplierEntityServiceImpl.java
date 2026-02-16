package com.fruitwarehouse.supplier.service.impl;

import com.fruitwarehouse.supplier.entity.Supplier;
import com.fruitwarehouse.common.exception.SupplierNotFoundException;
import com.fruitwarehouse.supplier.repository.SupplierRepository;
import com.fruitwarehouse.supplier.service.SupplierEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierEntityServiceImpl implements SupplierEntityService {

	private final SupplierRepository supplierRepository;

	@Override
	public Supplier getById(Long id) {
		return supplierRepository.findById(id)
				.orElseThrow(() -> new SupplierNotFoundException(id));
	}

	@Override
	public boolean existsById(Long id) {
		return supplierRepository.existsById(id);
	}

	@Override
	@Transactional
	public Supplier save(Supplier supplier) {
		return supplierRepository.save(supplier);
	}
}
