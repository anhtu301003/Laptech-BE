package com.project.LaptechBE.models;

import com.project.LaptechBE.Annotation.ValidSubCategory;
import com.project.LaptechBE.enums.CategoryEnum;
import com.project.LaptechBE.enums.SubCategoryEnum;
import com.project.LaptechBE.enums.TypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
@Validated
@ValidSubCategory
public class Product {
    @Id
    @Field("_id")
    private ObjectId id;

    @Indexed
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Indexed
    @NotBlank
    private CategoryEnum category;

    private SubCategoryEnum subCategory;

    @Indexed
    @NotBlank
    private String brand;

    @NotBlank
    private Number price;

    private Number starting_price;

    @Builder.Default
    private Number sale_percentage = 0;

    @NotBlank
    private Number stock;

    @NotBlank
    private List<String> images;

    private List<Color> colors;

    private List<Specification> specifications;

    @Builder.Default
    private String gift_value = "";

    private List<Review> reviews;

    @Builder.Default
    private Number averageRating = 0;

    @Builder.Default
    private Boolean isFeatured = Boolean.FALSE;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Color {
        @Id
        @Field("_id")
        private ObjectId id;

        @NotBlank
        private String title;

        @NotBlank
        private String hex;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Specification {
        @Id
        @Field("_id")
        private ObjectId id;

        @NotBlank
        private TypeEnum type;

        @NotBlank
        private String title;

        @NotBlank
        private String description;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Review {
        @Id
        @Field("_id")
        private ObjectId id;

        @DBRef
        private User userId;

        private Number rating;

        private String comment;

        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();
    }
}
