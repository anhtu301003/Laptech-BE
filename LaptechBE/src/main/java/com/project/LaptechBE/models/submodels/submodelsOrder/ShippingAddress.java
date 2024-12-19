package com.project.LaptechBE.models.submodels.submodelsOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddress {
    private String fullName;
    private String phone;
    private String detailAddress;
    private String city;
    @Builder.Default
    private String country = "Vietnam";
}
