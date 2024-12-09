package com.project.LaptechBE.DTO.ProductDTO;

import com.project.LaptechBE.enums.TypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private String name;
    private String description;
    private String category;
    private String subCategory;
    private String brand;
    private Number price;
    private Number stock;
    private Number sale_percentage;
    private Number starting_price;
    private List<String> images;
    private List<Color> colors;
    private List<Specification> specifications;
    private String gift_value;
    private List<Review> reviews;
    private Number averageRating;
    private Boolean isFeatured;

    @Builder
    @Data
    public static class Color {
        private String title;
        private String hex;
    }

    @Builder
    @Data
    public static class Specification {
        private TypeEnum type;
        private String title;
        private String description;
    }

    @Builder
    @Data
    public static class Review {
        private Number rating;
        private String comment;
    }
}
