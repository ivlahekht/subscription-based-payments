package hr.ht.workshop.fer.subscriptionbasedpayments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubscriptionBasedPaymentDto {

    @NotNull(message = "Subscriber ID is mandatory")
    private Long subscriberId;

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotBlank(message = "Subscription duration unit is mandatory and must not be blank")
    private String subscriptionDurationUnit;

    @NotNull(message = "Subscription duration amount is mandatory")
    private Long subscriptionDurationAmount;

    @NotNull(message = "Product price is mandatory")
    private Double price;
}
