package com.fruitwarehouse.repository;

import com.fruitwarehouse.AbstractIntegrationTest;
import com.fruitwarehouse.delivery.entity.Delivery;
import com.fruitwarehouse.delivery.entity.DeliveryItem;
import com.fruitwarehouse.delivery.entity.DeliveryStatus;
import com.fruitwarehouse.delivery.repository.DeliveryRepository;
import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.product.entity.ProductType;
import com.fruitwarehouse.supplier.entity.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeliveryRepositoryIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private DeliveryRepository deliveryRepository;

	private Supplier testSupplier;
	private Product testProduct;
	private DeliveryStatus createdStatus;

	@BeforeEach
	void setUp() {
		testSupplier = new Supplier();
		testSupplier.setName("Integration Test Supplier");
		testSupplier.setContactEmail("test@example.com");
		entityManager.persist(testSupplier);

		ProductType appleType = new ProductType(ProductType.Code.APPLE, "Apple", "Apple fruit");
		entityManager.persist(appleType);

		testProduct = new Product();
		testProduct.setName("Test Apple");
		testProduct.setProductType(appleType);
		testProduct.setVarietyName("Golden");
		entityManager.persist(testProduct);

		createdStatus = new DeliveryStatus(
				DeliveryStatus.Code.CREATED,
				"Created",
				"Delivery created",
				1
		);
		entityManager.persist(createdStatus);

		entityManager.flush();
	}

	@Test
	void findByIdWithItems_ShouldReturnDeliveryWithItems() {

		Delivery delivery = new Delivery();
		delivery.setSupplier(testSupplier);
		delivery.setDeliveryNumber(UUID.randomUUID());
		delivery.setDeliveryDate(LocalDateTime.now());
		delivery.setStatus(createdStatus);

		DeliveryItem item = new DeliveryItem();
		item.setDelivery(delivery);
		item.setProduct(testProduct);
		item.setWeight(new BigDecimal("10.5"));
		item.setUnitPrice(new BigDecimal("2.5"));
		item.setTotalPrice(new BigDecimal("26.25"));
		delivery.getItems().add(item);

		Delivery savedDelivery = entityManager.persist(delivery);
		entityManager.flush();
		entityManager.clear();

		Optional<Delivery> foundDelivery = deliveryRepository
				.findByIdWithItems(savedDelivery.getId());

		assertThat(foundDelivery).isPresent();
		assertThat(foundDelivery.get().getItems())
				.hasSize(1)
				.first()
				.extracting(DeliveryItem::getTotalPrice)
				.isEqualTo(new BigDecimal("26.25"));
	}
}