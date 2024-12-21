package com.project.LaptechBE.DTO.OrderDTO;

import com.project.LaptechBE.enums.PaymentMethodEnum;
import com.project.LaptechBE.models.submodels.submodelsOrder.OrderItem;
import com.project.LaptechBE.models.submodels.submodelsOrder.ShippingAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private List<OrderItem> items;
    private ShippingAddress shippingAddress;
    private String paymentMethod;
    private String couponCode;
    private String notes;
}
