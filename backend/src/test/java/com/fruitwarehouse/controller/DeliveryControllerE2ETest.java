package com.fruitwarehouse.controller;

import com.fruitwarehouse.AbstractIntegrationTest;
import com.fruitwarehouse.delivery.controller.dto.request.CreateDeliveryRequest;
import com.fruitwarehouse.delivery.controller.dto.request.DeliveryItemRequest;
import com.fruitwarehouse.delivery.entity.DeliveryStatus;
import com.fruitwarehouse.delivery.repository.DeliveryStatusRepository;
import com.fruitwarehouse.product.entity.Product;
import com.fruitwarehouse.product.entity.ProductType;
import com.fruitwarehouse.product.repository.ProductRepository;
import com.fruitwarehouse.product.repository.ProductTypeRepository;
import com.fruitwarehouse.supplier.entity.Supplier;
import com.fruitwarehouse.supplier.entity.SupplierProductPrice;
import com.fruitwarehouse.supplier.repository.SupplierProductPriceRepository;
import com.fruitwarehouse.supplier.repository.SupplierRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DeliveryControllerE2ETest extends AbstractIntegrationTest {

	@LocalServerPort
	private Integer port;

	@Autowired
	private SupplierRepository supplierRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductTypeRepository productTypeRepository;

	@Autowired
	private DeliveryStatusRepository deliveryStatusRepository;

	@Autowired
	private SupplierProductPriceRepository priceRepository;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost:" + port;
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		createTestData();
	}

	private void createTestData() {
		// тип продукта
		ProductType appleType = new ProductType();
		appleType.setCode(ProductType.Code.APPLE);
		appleType.setName("Apple");
		appleType.setDescription("Apple fruit");
		productTypeRepository.save(appleType);

		// поставщика
		Supplier supplier = new Supplier();
		supplier.setName("Test Supplier");
		supplier.setContactEmail("test@example.com");
		supplierRepository.save(supplier);

		// продукты
		Product product1 = new Product();
		product1.setName("Golden Apple");
		product1.setProductType(appleType);
		product1.setVarietyName("Golden");
		productRepository.save(product1);

		Product product2 = new Product();
		product2.setName("Red Apple");
		product2.setProductType(appleType);
		product2.setVarietyName("Red");
		productRepository.save(product2);

		// статус доставки
		DeliveryStatus createdStatus = new DeliveryStatus();
		createdStatus.setCode(DeliveryStatus.Code.CREATED);
		createdStatus.setName("Created");
		createdStatus.setDescription("Delivery created");
		deliveryStatusRepository.save(createdStatus);

		// активные цены
		SupplierProductPrice price1 = new SupplierProductPrice();
		price1.setSupplier(supplier);
		price1.setProduct(product1);
		price1.setPrice(new BigDecimal("2.5"));
		price1.setEffectiveFrom(LocalDate.now().minusDays(1));
		price1.setEffectiveTo(LocalDate.now().plusDays(30));
		priceRepository.save(price1);

		SupplierProductPrice price2 = new SupplierProductPrice();
		price2.setSupplier(supplier);
		price2.setProduct(product2);
		price2.setPrice(new BigDecimal("3.0"));
		price2.setEffectiveFrom(LocalDate.now().minusDays(1));
		price2.setEffectiveTo(LocalDate.now().plusDays(30));
		priceRepository.save(price2);
	}

	private void createDelivery(LocalDateTime deliveryDate) {
		Long supplierId = supplierRepository.findAll().get(0).getId();
		List<Product> products = productRepository.findAll();
		Long product1Id = products.get(0).getId();
		Long product2Id = products.get(1).getId();

		CreateDeliveryRequest request = new CreateDeliveryRequest(
				supplierId,
				deliveryDate,
				List.of(
						new DeliveryItemRequest(product1Id, new BigDecimal("10.5")),
						new DeliveryItemRequest(product2Id, new BigDecimal("5.2"))
				)
		);

		given()
				.contentType(ContentType.JSON)
				.body(request)
				.when()
				.post("/api/v1/deliveries")
				.then()
				.statusCode(201);
	}

	@Test
	void createDelivery_ShouldReturnCreatedDelivery() {
		Long supplierId = supplierRepository.findAll().get(0).getId();
		List<Product> products = productRepository.findAll();
		Long product1Id = products.get(0).getId();
		Long product2Id = products.get(1).getId();

		CreateDeliveryRequest request = new CreateDeliveryRequest(
				supplierId,
				LocalDateTime.now().plusDays(1),
				List.of(
						new DeliveryItemRequest(product1Id, new BigDecimal("10.5")),
						new DeliveryItemRequest(product2Id, new BigDecimal("5.2"))
				)
		);

		given()
				.contentType(ContentType.JSON)
				.body(request)
				.when()
				.post("/api/v1/deliveries")
				.then()
				.statusCode(201)
				.body("id", notNullValue())
				.body("deliveryNumber", notNullValue())
				.body("items", hasSize(2))
				.body("totalWeight", greaterThan(0f))
				.body("totalCost", greaterThan(0f));

	}

	@Test
	void getAllDeliveries_ShouldReturnList() {
		createDelivery(LocalDateTime.now());

		given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/v1/deliveries")
				.then()
				.statusCode(200)
				.body(".", is(not(empty())));
	}
}