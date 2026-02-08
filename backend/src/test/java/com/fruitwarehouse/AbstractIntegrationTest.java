package com.fruitwarehouse;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.Contexts;
import liquibase.LabelExpression;

@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {

		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);

		registry.add("spring.liquibase.enabled", () -> "false");

		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

		registry.add("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults", () -> "false");
		registry.add("spring.jpa.properties.hibernate.check_nullability", () -> "false");
		registry.add("spring.jpa.properties.hibernate.validator.apply_to_ddl", () -> "false");

		runLiquibaseMigrations(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword(),
				"db/changelog/db.changelog-master.yaml");
	}

	private static void runLiquibaseMigrations(String jdbcUrl, String username, String password, String changelogPath) {
		try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
			Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(conn));
			try (ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor()) {
				Liquibase liquibase = new Liquibase(changelogPath, resourceAccessor, database);
				liquibase.update(new Contexts(), new LabelExpression());
			}
		} catch (Exception e) {
			throw new IllegalStateException("Не удалось запустить Liquibase перед поднятием контекста", e);
		}
	}
}
