package com.project.LaptechBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.LaptechBE.enums.StatusEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(name = "user_status_idx", def = "{'userId': 1, 'status': 1}"),
        @CompoundIndex(name = "last_active_idx", def = "{'lastActive': 1}")
})
public class Cart {
    @Id
    @Field("_id")
    @JsonProperty("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotBlank
    private String userId;

    private List<ProductItem> products;

    @Builder.Default
    @NotBlank
    private Number totalPrice = 0;

    @Builder.Default
    private StatusEnum status = StatusEnum.active;

    @Indexed(expireAfter = "7d")
    @Builder.Default
    private LocalDateTime lastActive = LocalDateTime.now();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {

        private String productId;

        @NotBlank
        @Min(value = 1)
        private Number quantity;

        private Number price;

        private String name;

        private String image;

        private Number stock;

        private Number subtotal;

        private Specification Specifications;

        @Builder
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        private static class Specification {
            private String color;
            private String size;
        }
    }

//    public void calculateTotalPrice() {
//        this.totalPrice = this.getProducts().stream().mapToDouble(
//                productItem -> {
//                    Product product = productItem.getProductId();
//                    if(product != null) {
//                        Number productPrice = product.getPrice();
//                        Number productQuantity = productItem.getQuantity();
//                        return productPrice.doubleValue() * productQuantity.doubleValue();
//                    }
//                    return 0;
//                })
//                .sum();
//        this.lastActive = LocalDateTime.now();
//    }
}
