package com.project.LaptechBE.models.submodels.submodelsOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificationOrderItem {
    private String cpu;
    private String ram;
    private String storage;
    private String color;
}
