package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import hr.ht.workshop.fer.subscriptionbasedpayments.exception.PaymentUnsuccessfulOnPaymentGatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile(Profiles.NAIVE_IMPLEMENTATION)
@RequiredArgsConstructor
@Slf4j
public class NaivePaymentProcessingService implements PaymentProcessingService {

    private static final long DEFAULT_SLEEP_TIME_MILLIS = 1000;
    private static final int GRACE_PERIOD_DURATION_SECONDS = 1;

    private final PaymentSchedulingService paymentSchedulingService;
    private final PaymentGatewayService paymentGatewayService;
    private final NotificationService notificationService;
    private final ProductService productService;

    @Override
    @Async
    public void processPayment(SubscriptionBasedPayment subscriptionBasedPayment) {
        log.info("Processing subscription based payment ID {} naively", subscriptionBasedPayment.getId());

        while (true) {
            paymentSchedulingService.scheduleNextPaymentDueDateTime(subscriptionBasedPayment);
            waitUntil(subscriptionBasedPayment.getNextPaymentDueDateTime());
            log.info("Next payment due date time reached for subscription based payment ID {}", subscriptionBasedPayment.getId());

            try {
                paymentGatewayService.processPaymentOnPaymentGateway(subscriptionBasedPayment);
                log.info("Payment successful for subscription based payment ID {}", subscriptionBasedPayment.getId());
            } catch (PaymentUnsuccessfulOnPaymentGatewayException e) {
                log.warn("Payment unsuccessful for subscription based payment ID {}. Notifying subscriber", subscriptionBasedPayment.getId());
                notificationService.notifySubscriberAboutUnsuccessfulPayment(subscriptionBasedPayment);
                boolean productSuspended = activateGracePeriodAndRetryPayment(subscriptionBasedPayment);
                if(productSuspended) { break; }
            }
        }

        log.info("Product ID {} suspended because it was not paid for. Ending process subscription based payment ID {}",
                subscriptionBasedPayment.getProductId(), subscriptionBasedPayment.getId());
    }

    private void waitUntil(LocalDateTime localDateTime) {
        log.info("Waiting until date time {} is reached", localDateTime);

        while (LocalDateTime.now().isBefore(localDateTime)) {
            try {
                log.debug("{} not yet reached. Sleeping for {} milliseconds", localDateTime, DEFAULT_SLEEP_TIME_MILLIS);
                Thread.sleep(DEFAULT_SLEEP_TIME_MILLIS);
            } catch (InterruptedException ex) {
                log.warn("Interrupted waiting for date time {}!", localDateTime);
                Thread.currentThread().interrupt();
            }
            log.debug("Woke up! Checking whether {} is reached", localDateTime);
        }

        log.info("Date time {} reached! Continuing", localDateTime);
    }

    private boolean activateGracePeriodAndRetryPayment(SubscriptionBasedPayment subscriptionBasedPayment) {
        log.info("Activating grace period for product ID {} and retrying the payment", subscriptionBasedPayment.getProductId());
        productService.activateGracePeriod(subscriptionBasedPayment.getProductId());
        log.info("Grace period for product ID {} activated. Retrying payment in {} second(s)",
                subscriptionBasedPayment.getProductId(), GRACE_PERIOD_DURATION_SECONDS);
        waitUntil(LocalDateTime.now().plusSeconds(GRACE_PERIOD_DURATION_SECONDS));
        log.info("Grace period expired. Retrying payment for product ID {}!", subscriptionBasedPayment.getProductId());

        try {
            paymentGatewayService.processPaymentOnPaymentGateway(subscriptionBasedPayment);
            log.info("Payment successful for subscription based payment ID {}. Product will not be suspended", subscriptionBasedPayment.getId());
            return false;
        } catch (PaymentUnsuccessfulOnPaymentGatewayException e) {
            log.info("Payment unsuccessful for subscription based payment ID {}. Product ID {} was in grace period, suspending it",
                    subscriptionBasedPayment.getId(), subscriptionBasedPayment.getProductId());
            productService.suspendProduct(subscriptionBasedPayment.getProductId());
            log.info("Product ID {} suspended as it was not paid for after the grace period expiration", subscriptionBasedPayment.getProductId());
            return true;
        }
    }
}
