package com.project.LaptechBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.LaptechBE.Annotation.ValidSubCategory;
import com.project.LaptechBE.enums.CategoryEnum;
import com.project.LaptechBE.enums.SubCategoryEnum;
import com.project.LaptechBE.enums.TypeEnum;
import com.project.LaptechBE.models.submodels.submodelsProduct.ProductColor;
import com.project.LaptechBE.models.submodels.submodelsProduct.ProductReview;
import com.project.LaptechBE.models.submodels.submodelsProduct.ProductSpecification;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
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
@CompoundIndexes({
        @CompoundIndex(name = "name_text_brand_text_category_text", def = "{'userId': 'text', 'brand': 'text', 'category': 'text'}")
})
public class Product {
    @Id
    @Field("_id")
    @JsonProperty("_id")  // Giữ tên trường là _id trong JSON trả về
    @JsonSerialize(using = ToStringSerializer.class)  // Chuyển ObjectId thành chuỗi khi trả về
    private ObjectId id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private CategoryEnum category;

    private SubCategoryEnum subCategory;

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

    private List<ProductColor> colors;

    private List<ProductSpecification> specifications;

    @Builder.Default
    private String gift_value = "";

    private List<ProductReview> reviews;

    @Builder.Default
    private Number averageRating = 0;

    @Builder.Default
    private Boolean isFeatured = Boolean.FALSE;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
