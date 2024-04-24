package hr.ht.workshop.fer.subscriptionbasedpayments.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SubscriptionBasedPayment {

    private UUID id;
    private Long subscriberId;
    private Long productId;
    private Duration subscriptionDuration;
    private Double price;
    private LocalDateTime nextPaymentDueDateTime;
}
