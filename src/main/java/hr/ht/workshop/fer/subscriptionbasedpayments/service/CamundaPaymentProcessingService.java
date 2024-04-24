package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
public class CamundaPaymentProcessingService implements PaymentProcessingService {

    @Override
    public void processPayment(SubscriptionBasedPayment subscriptionBasedPayment) {
        throw new UnsupportedOperationException("To be implemented!");
    }
}
