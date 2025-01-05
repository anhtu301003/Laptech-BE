package com.project.LaptechBE.DTO.ReviewDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.LaptechBE.models.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    @DBRef(lazy = true)
    @JsonIgnoreProperties({"reviews"})
    private Product productId;
    private int rating;
    private String comment;
}
