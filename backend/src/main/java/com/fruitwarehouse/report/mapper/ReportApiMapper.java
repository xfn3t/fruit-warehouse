package com.fruitwarehouse.report.mapper;

import com.fruitwarehouse.report.controller.dto.response.DetailedReportItemResponse;
import com.fruitwarehouse.report.controller.dto.response.ReportItemResponse;
import com.fruitwarehouse.report.repository.dto.DetailedReportItemDto;
import com.fruitwarehouse.supplier.repository.dto.ReportItemDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportApiMapper {
	ReportItemResponse toReportItemResponse(ReportItemDto dto);
	List<ReportItemResponse> toReportItemResponseList(List<ReportItemDto> dtos);

	DetailedReportItemResponse toDetailedReportItemResponse(DetailedReportItemDto dto);
	List<DetailedReportItemResponse> toDetailedReportItemResponseList(List<DetailedReportItemDto> dtos);
}
