package com.project.LaptechBE.models;

import com.project.LaptechBE.enums.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specification {
    private TypeEnum type;
    private String title;
    private String description;
}
