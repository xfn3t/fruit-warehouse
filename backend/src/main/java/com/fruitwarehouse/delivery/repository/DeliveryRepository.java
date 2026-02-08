package com.fruitwarehouse.delivery.repository;

import com.fruitwarehouse.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

	Optional<Delivery> findByDeliveryNumber(UUID deliveryNumber);

	@Query("SELECT d FROM Delivery d LEFT JOIN FETCH d.items WHERE d.id = :id")
	Optional<Delivery> findByIdWithItems(@Param("id") Long id);

	@Query("""
        SELECT d FROM Delivery d
        LEFT JOIN FETCH d.supplier
        LEFT JOIN FETCH d.items i
        LEFT JOIN FETCH i.product
        WHERE d.deliveryDate BETWEEN :startDate AND :endDate
        """)
	List<Delivery> findByDeliveryDateBetweenWithDetails(
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);

	List<Delivery> findBySupplierId(Long supplierId);

	@Query("""
        SELECT d FROM Delivery d
        LEFT JOIN FETCH d.supplier
        LEFT JOIN FETCH d.items i
        LEFT JOIN FETCH i.product
        WHERE d.supplier.id = :supplierId
        """)
	List<Delivery> findBySupplierIdWithDetails(@Param("supplierId") Long supplierId);
}
