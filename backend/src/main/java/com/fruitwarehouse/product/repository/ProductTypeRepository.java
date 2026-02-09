package com.fruitwarehouse.product.repository;

import com.fruitwarehouse.product.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
	Optional<ProductType> findByCode(ProductType.Code code);
}