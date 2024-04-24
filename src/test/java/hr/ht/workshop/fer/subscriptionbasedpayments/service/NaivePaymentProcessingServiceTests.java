package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.NAIVE_IMPLEMENTATION)
class NaivePaymentProcessingServiceTests extends PaymentProcessingServiceTests { }