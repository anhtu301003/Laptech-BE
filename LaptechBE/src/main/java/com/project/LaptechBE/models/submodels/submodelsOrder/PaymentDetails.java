package com.project.LaptechBE.models.submodels.submodelsOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {
    private String transactionId;
    private Date paymentTime;
    private String bank;
    private String cardLastFour;
}
