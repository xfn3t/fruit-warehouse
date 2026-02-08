package com.fruitwarehouse.report.repository;

import com.fruitwarehouse.report.repository.dto.DetailedReportItemDto;
import com.fruitwarehouse.supplier.repository.dto.ReportItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public List<DetailedReportItemDto> findDetailedReportData(
			LocalDateTime startDate,
			LocalDateTime endDate
	) {

		String sql = """
			SELECT
				s.name as supplierName,
				d.delivery_number as deliveryNumber,
				d.delivery_date as deliveryDate,
				p.name as productName,
				pt.name as productType,
				p.variety_name as variety,
				di.weight,
				di.unit_price as unitPrice,
				di.total_price as totalPrice
			FROM delivery_items di
			JOIN deliveries d ON di.delivery_id = d.id
			JOIN suppliers s ON d.supplier_id = s.id
			JOIN products p ON di.product_id = p.id
			JOIN product_types pt ON p.product_type_id = pt.id
			WHERE d.delivery_date BETWEEN :startDate AND :endDate
			ORDER BY d.delivery_date DESC, s.name, pt.name, p.variety_name
			""";


		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);

		return jdbcTemplate.query(sql, params,
				new BeanPropertyRowMapper<>(DetailedReportItemDto.class));
	}

	public List<ReportItemDto> findSummaryReportData(
			LocalDateTime startDate,
			LocalDateTime endDate
	) {

		String sql = """
            SELECT
                s.name as supplierName,
                pt.name as productType,
                p.variety_name as variety,
                SUM(di.weight) as totalWeight,
                SUM(di.total_price) as totalCost
            FROM delivery_items di
            JOIN deliveries d ON di.delivery_id = d.id
            JOIN suppliers s ON d.supplier_id = s.id
            JOIN products p ON di.product_id = p.id
            JOIN product_types pt ON p.product_type_id = pt.id
            WHERE d.delivery_date BETWEEN :startDate AND :endDate
            GROUP BY s.name, pt.name, p.variety_name
            ORDER BY s.name, pt.name, p.variety_name
            """;

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate);
		params.addValue("endDate", endDate);

		return jdbcTemplate.query(sql, params,
				new BeanPropertyRowMapper<>(ReportItemDto.class));
	}
}