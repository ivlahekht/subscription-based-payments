package hr.ht.workshop.fer.subscriptionbasedpayments.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionBasedPaymentDto {

    private String id;
    private Long subscriberId;
    private Long productId;
    private String subscriptionDurationUnit;
    private Long subscriptionDurationAmount;
    private Double price;
}
