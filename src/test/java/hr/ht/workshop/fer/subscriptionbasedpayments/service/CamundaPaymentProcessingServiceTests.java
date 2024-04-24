package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
class CamundaPaymentProcessingServiceTests extends PaymentProcessingServiceTests { }