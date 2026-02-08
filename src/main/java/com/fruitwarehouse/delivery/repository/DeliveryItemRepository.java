package com.fruitwarehouse.delivery.repository;

import com.fruitwarehouse.delivery.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
}
