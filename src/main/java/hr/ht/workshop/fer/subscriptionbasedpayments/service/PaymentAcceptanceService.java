package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.dto.CreateSubscriptionBasedPaymentDto;
import hr.ht.workshop.fer.subscriptionbasedpayments.dto.SubscriptionBasedPaymentDto;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAcceptanceService {

    private final PaymentProcessingService paymentProcessingService;

    public SubscriptionBasedPaymentDto acceptPaymentCreation(CreateSubscriptionBasedPaymentDto createSubscriptionBasedPaymentDto) {
        log.info("Creating subscription based payment before accepting it");

        SubscriptionBasedPayment subscriptionBasedPayment = SubscriptionBasedPayment.builder()
                .id(UUID.randomUUID())
                .subscriberId(createSubscriptionBasedPaymentDto.getSubscriberId())
                .productId(createSubscriptionBasedPaymentDto.getProductId())
                .subscriptionDuration(computeSubsciptionDuration(createSubscriptionBasedPaymentDto))
                .price(createSubscriptionBasedPaymentDto.getPrice())
                .build();

        log.info("Subscription based payment created. Subscription based payment is {}. Submitting it for asynchronous processing", subscriptionBasedPayment);
        paymentProcessingService.processPayment(subscriptionBasedPayment);
        log.info("Subscription based payment ID {} successfully submitted for asynchronous processing and accepted", subscriptionBasedPayment.getId());
        return mapSubscriptionBasedPaymentToSubscriptionBasedPaymentDto(
                subscriptionBasedPayment, createSubscriptionBasedPaymentDto.getSubscriptionDurationUnit(), createSubscriptionBasedPaymentDto.getSubscriptionDurationAmount());
    }

    private Duration computeSubsciptionDuration(CreateSubscriptionBasedPaymentDto createSubscriptionBasedPaymentDto) {
        log.info("Computing subscription duration. Subscription duration unit is {}, and subscription duration amount is {}",
                createSubscriptionBasedPaymentDto.getSubscriptionDurationUnit(), createSubscriptionBasedPaymentDto.getSubscriptionDurationAmount());

        try {
            return Duration.of(createSubscriptionBasedPaymentDto.getSubscriptionDurationAmount(), ChronoUnit.valueOf(createSubscriptionBasedPaymentDto.getSubscriptionDurationUnit().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            log.error("Could not compute subscription duration. Subscription duration unit {} is invalid", createSubscriptionBasedPaymentDto.getSubscriptionDurationUnit());
            throw new IllegalArgumentException("Could not compute subscription duration, duration unit is invalid.", ex);
        } catch (Exception ex) {
            log.error("An exception has occurred when computing subscription duration!", ex);
            throw ex;
        }
    }

    private SubscriptionBasedPaymentDto mapSubscriptionBasedPaymentToSubscriptionBasedPaymentDto(
            SubscriptionBasedPayment subscriptionBasedPayment, String subscriptionDurationUnit, Long subscriptionDurationAmount) {
        log.info("Mapping subscription based payment ID {} to the subscription based payment response data transfer object", subscriptionBasedPayment.getId());
        SubscriptionBasedPaymentDto subscriptionBasedPaymentDto = SubscriptionBasedPaymentDto.builder()
                .id(subscriptionBasedPayment.getId().toString())
                .subscriberId(subscriptionBasedPayment.getSubscriberId())
                .productId(subscriptionBasedPayment.getProductId())
                .subscriptionDurationUnit(subscriptionDurationUnit)
                .subscriptionDurationAmount(subscriptionDurationAmount)
                .price(subscriptionBasedPayment.getPrice())
                .build();
        log.info("Subscription based payment response mapped and is {}", subscriptionBasedPaymentDto);
        return subscriptionBasedPaymentDto;
    }
}