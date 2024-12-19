package com.project.LaptechBE.models.submodels.submodelsOrder;

import com.project.LaptechBE.models.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class OrderItem {
    @DBRef(lazy = true)
    private Product ProductId;

    private String name;
    private Number price;
    private int quantity;
    private List<String> images;
    private SpecificationOrderItem specifications;
}
