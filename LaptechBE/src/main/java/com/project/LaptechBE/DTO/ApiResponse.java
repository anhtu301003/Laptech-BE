package com.project.LaptechBE.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.LaptechBE.filters.ZeroOrNullFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private String status;
    private String message;
    private String access_token;
    private Object data;
    private Object details;
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = ZeroOrNullFilter.class)
    private int count;
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = ZeroOrNullFilter.class)
    private int totalPages;
}
