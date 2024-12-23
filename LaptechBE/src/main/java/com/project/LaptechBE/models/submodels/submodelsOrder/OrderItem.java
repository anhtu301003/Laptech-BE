package com.project.LaptechBE.models.submodels.submodelsOrder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.project.LaptechBE.models.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class OrderItem {
    @Id
    @Field("_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @DBRef(lazy = true)
    private Product ProductId;
    private String name;
    private Number price;
    private int quantity;
    private List<String> images;
}
