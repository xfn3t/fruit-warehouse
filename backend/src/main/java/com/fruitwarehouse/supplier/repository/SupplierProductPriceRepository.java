package com.fruitwarehouse.supplier.repository;

import com.fruitwarehouse.supplier.entity.SupplierProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierProductPriceRepository extends JpaRepository<SupplierProductPrice, Long> {

	@Query("""
        SELECT spp FROM SupplierProductPrice spp
        WHERE spp.supplier.id = :supplierId
        AND spp.product.id = :productId
        AND spp.effectiveFrom <= :date
        AND (spp.effectiveTo IS NULL OR spp.effectiveTo >= :date)
        ORDER BY spp.effectiveFrom DESC
        LIMIT 1
        """)
	Optional<SupplierProductPrice> findActivePrice(
			@Param("supplierId") Long supplierId,
			@Param("productId") Long productId,
			@Param("date") LocalDate date
	);

	@Query("""
        SELECT spp FROM SupplierProductPrice spp
        WHERE spp.supplier.id = :supplierId
        AND spp.product.id = :productId
        AND spp.effectiveFrom <= CURRENT_DATE
        AND (spp.effectiveTo IS NULL OR spp.effectiveTo >= CURRENT_DATE)
        """)
	Optional<SupplierProductPrice> findCurrentPrice(
			@Param("supplierId") Long supplierId,
			@Param("productId") Long productId
	);

	@Query("SELECT spp FROM SupplierProductPrice spp WHERE spp.supplier.id = :supplierId")
	List<SupplierProductPrice> findBySupplierId(@Param("supplierId") Long supplierId);

	@Query("""
        SELECT spp FROM SupplierProductPrice spp
        WHERE spp.supplier.id = :supplierId
        AND spp.product.id = :productId
        """)
	List<SupplierProductPrice> findBySupplierIdAndProductId(
			@Param("supplierId") Long supplierId,
			@Param("productId") Long productId
	);

	@Query("""
        SELECT spp FROM SupplierProductPrice spp
        WHERE spp.id = :id
        AND spp.supplier.id = :supplierId
        """)
	Optional<SupplierProductPrice> findByIdAndSupplierId(
			@Param("id") Long id,
			@Param("supplierId") Long supplierId
	);

	@Query("""
        SELECT spp FROM SupplierProductPrice spp
        WHERE spp.supplier.id = :supplierId
        AND spp.effectiveFrom <= :date
        AND (spp.effectiveTo IS NULL OR spp.effectiveTo >= :date)
        """)
	List<SupplierProductPrice> findActiveBySupplierId(
			@Param("supplierId") Long supplierId,
			@Param("date") LocalDate date
	);
}