package hr.ht.workshop.fer.subscriptionbasedpayments.controller.definition;

import hr.ht.workshop.fer.subscriptionbasedpayments.dto.CreateSubscriptionBasedPaymentDto;
import hr.ht.workshop.fer.subscriptionbasedpayments.dto.SubscriptionBasedPaymentDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(Endpoints.SUBSCRIPTION_BASED_PAYMENTS_V1)
public interface SubscriptionBasedPaymentDefinition {

    @PostMapping
    ResponseEntity<SubscriptionBasedPaymentDto> acceptPaymentCreation(@Valid @RequestBody CreateSubscriptionBasedPaymentDto createSubscriptionBasedPaymentDto);
}
