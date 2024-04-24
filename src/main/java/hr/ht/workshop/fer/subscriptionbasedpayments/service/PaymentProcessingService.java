package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;

public interface PaymentProcessingService {

    void processPayment(SubscriptionBasedPayment subscriptionBasedPayment);
}
