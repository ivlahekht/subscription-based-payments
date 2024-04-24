package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentSchedulingService {

    public void scheduleNextPaymentDueDateTime(SubscriptionBasedPayment subscriptionBasedPayment) {
        log.info("Scheduling next payment due date time for subscription based payment ID {}", subscriptionBasedPayment.getId());
        LocalDateTime nextPaymentDueDateTime = LocalDateTime.now().plus(subscriptionBasedPayment.getSubscriptionDuration());
        subscriptionBasedPayment.setNextPaymentDueDateTime(nextPaymentDueDateTime);
        log.info("Next payment due date scheduled for {}", nextPaymentDueDateTime);
    }
}
