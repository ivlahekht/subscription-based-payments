package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway.PaymentGatewayClient;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import hr.ht.workshop.fer.subscriptionbasedpayments.exception.PaymentUnsuccessfulOnPaymentGatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final PaymentGatewayClient paymentGatewayClient;

    public void processPaymentOnPaymentGateway(SubscriptionBasedPayment subscriptionBasedPayment) {
        log.info("Processing subscription based payment ID {} on payment gateway. {} euros will be deducted",
                subscriptionBasedPayment.getId(), subscriptionBasedPayment.getPrice());

        try {
            paymentGatewayClient.deductMoney(subscriptionBasedPayment.getSubscriberId(), subscriptionBasedPayment.getPrice());
            log.info("{} euros successfully deducted for subscriber ID {}", subscriptionBasedPayment.getPrice(), subscriptionBasedPayment.getSubscriberId());
        } catch (Exception e) {
            log.error("An error has occurred on payment gateway. Subscriber card has either expired, there are no enough funds, and payment method is missing");
            throw new PaymentUnsuccessfulOnPaymentGatewayException("Could not deduct money on payment gateway.", e);
        }
    }
}
