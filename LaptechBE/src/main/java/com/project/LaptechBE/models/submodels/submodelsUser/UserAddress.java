package com.project.LaptechBE.models.submodels.submodelsUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String label;
}
