package com.fruitwarehouse;

import com.fruitwarehouse.controller.DeliveryControllerE2ETest;
import com.fruitwarehouse.repository.DeliveryRepositoryIntegrationTest;
import com.fruitwarehouse.service.dto.impl.DeliveryServiceImplUnitTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Complete Test Suite for Fruit Warehouse")
@SelectClasses({
		FruitWarehouseApplicationTests.class,
		DeliveryControllerE2ETest.class,
		DeliveryRepositoryIntegrationTest.class,
		DeliveryServiceImplUnitTest.class
})
public class AllTestsSuite {
}