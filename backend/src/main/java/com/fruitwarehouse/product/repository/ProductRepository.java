package com.fruitwarehouse.product.repository;

import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.product.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p FROM Product p WHERE p.productType.code = :productTypeCode AND p.varietyName = :varietyName")
	Optional<Product> findByProductTypeCodeAndVarietyName(
			@Param("productTypeCode") ProductType.Code productTypeCode,
			@Param("varietyName") String varietyName
	);

	@Query("SELECT p FROM Product p WHERE p.productType.code = :productTypeCode")
	List<Product> findByProductTypeCode(@Param("productTypeCode") ProductType.Code productTypeCode);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.productType.code = :productTypeCode AND p.varietyName = :varietyName")
	boolean existsByProductTypeCodeAndVarietyName(
			@Param("productTypeCode") ProductType.Code productTypeCode,
			@Param("varietyName") String varietyName
	);

}
