package com.project.LaptechBE.models;

import com.project.LaptechBE.enums.PaymentMethodEnum;
import com.project.LaptechBE.enums.PaymentStatusEnum;
import com.project.LaptechBE.enums.StatusOrderEnum;
import com.project.LaptechBE.models.submodels.submodelsOrder.OrderItem;
import com.project.LaptechBE.models.submodels.submodelsOrder.PaymentDetails;
import com.project.LaptechBE.models.submodels.submodelsOrder.ShippingAddress;
import com.project.LaptechBE.models.submodels.submodelsOrder.StatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
@CompoundIndexes({
        @CompoundIndex(name = "userId_1_createdAt_-1",def = "{'userId':1,'createdAt:-1'}"),
        @CompoundIndex(name = "status_1_createAt_-1",def = "{'status':1,'createAt':-1}")
})
public class Order {
    private Object id;
    private List<OrderItem> items;

    @Builder.Default
    private StatusOrderEnum status = StatusOrderEnum.pending;

    private ShippingAddress shippingAddress;

    private PaymentMethodEnum paymentMethod;

    @Builder.Default
    private PaymentStatusEnum paymentStatus = PaymentStatusEnum.pending;

    private PaymentDetails paymentDetails;

    private Number shippingFee;

    private Number subtotal;

    @Builder.Default
    private Number discount = 0;

    private Number total;

    private String couponCode;

    private String notes;

    private String trackingNumber;

    private String cancelReason;

    private Date cancelledAt;

    private Date deliveryAt;

    private List<StatusHistory> statusHistory;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}


