package com.project.LaptechBE.models.submodels.submodelsCart;

import com.project.LaptechBE.models.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProductItem {
    @DBRef(lazy = true)
    private Product productId;

    @NotBlank
    @Min(value = 1)
    private Number quantity;

    private Number price;

    private String name;

    private List<String> images;

    private Number stock;

    private Number subtotal;

    private List<CartProductItemSpecification> Specifications;
}
