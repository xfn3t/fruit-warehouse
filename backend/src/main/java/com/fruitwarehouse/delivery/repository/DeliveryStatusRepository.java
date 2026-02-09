package com.fruitwarehouse.delivery.repository;

import com.fruitwarehouse.delivery.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {
	Optional<DeliveryStatus> findByCode(DeliveryStatus.Code code);
}