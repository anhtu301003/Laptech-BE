package com.project.LaptechBE.models;

import com.project.LaptechBE.enums.CategoryEnum;
import com.project.LaptechBE.enums.SubCategoryEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    @NotBlank
    private ObjectId id;
    @Indexed
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Indexed
    private CategoryEnum category;

    private SubCategoryEnum subCategory;
    @Indexed
    private String brand;
    private Number price;
    private String starting_price;
    private Number sale_percentage;
    private Number stock;
    private ArrayList<String> images;
    private ArrayList<Color> colors;
    private ArrayList<Specification> specifications;
    private String gift_value;
    private ArrayList<Review> reviews;
    private Number averageRating;
    private Boolean isFeatured;
}
