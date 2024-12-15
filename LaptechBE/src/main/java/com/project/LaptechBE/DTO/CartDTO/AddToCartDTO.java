package com.project.LaptechBE.DTO.CartDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartDTO {
    private String productId;
    private int quantity;
}
