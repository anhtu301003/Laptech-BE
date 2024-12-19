package com.project.LaptechBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.LaptechBE.enums.StatusEnum;
import com.project.LaptechBE.models.submodels.submodelsCart.CartProductItem;
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
        @CompoundIndex(name = "userId_1_status_1", def = "{'userId': 1, 'status': 1}"),
})
public class Cart {
    @Id
    @Field("_id")
    @JsonProperty("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotBlank
    @DBRef(lazy = true)
    private User userId;

    private List<CartProductItem> products;

    @Builder.Default
    @NotBlank
    private Number totalPrice = 0;

    @Builder.Default
    private StatusEnum status = StatusEnum.active;

    @Indexed(expireAfter = "7d",name = "lastActive_1")
    @Builder.Default
    private LocalDateTime lastActive = LocalDateTime.now();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
