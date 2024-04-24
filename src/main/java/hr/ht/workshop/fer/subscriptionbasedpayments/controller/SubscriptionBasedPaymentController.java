package hr.ht.workshop.fer.subscriptionbasedpayments.controller;

import hr.ht.workshop.fer.subscriptionbasedpayments.controller.definition.SubscriptionBasedPaymentDefinition;
import hr.ht.workshop.fer.subscriptionbasedpayments.dto.CreateSubscriptionBasedPaymentDto;
import hr.ht.workshop.fer.subscriptionbasedpayments.dto.SubscriptionBasedPaymentDto;
import hr.ht.workshop.fer.subscriptionbasedpayments.service.PaymentAcceptanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SubscriptionBasedPaymentController implements SubscriptionBasedPaymentDefinition {

    private final PaymentAcceptanceService subscriptionBasedPaymentService;

    @Override
    public ResponseEntity<SubscriptionBasedPaymentDto> acceptPaymentCreation(CreateSubscriptionBasedPaymentDto createSubscriptionBasedPaymentDto) {
        log.info("Accepting subscription based payment creation for subscriber ID {} and product ID {}. " +
                        "Subscription duration unit is {}, and the subscription duration amount is {}",
                createSubscriptionBasedPaymentDto.getSubscriberId(),
                createSubscriptionBasedPaymentDto.getProductId(),
                createSubscriptionBasedPaymentDto.getSubscriptionDurationUnit(),
                createSubscriptionBasedPaymentDto.getSubscriptionDurationAmount());

        SubscriptionBasedPaymentDto acceptedSubscriptionBasedPaymentDto =
                subscriptionBasedPaymentService.acceptPaymentCreation(createSubscriptionBasedPaymentDto);

        log.info("Subscription based payment has been accepted. Its ID is {}", acceptedSubscriptionBasedPaymentDto.getId());
        return ResponseEntity
                .accepted()
                .body(acceptedSubscriptionBasedPaymentDto);
    }
}
