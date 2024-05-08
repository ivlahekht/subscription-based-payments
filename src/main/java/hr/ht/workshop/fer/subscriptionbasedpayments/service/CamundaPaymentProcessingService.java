package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
@Slf4j
public class CamundaPaymentProcessingService implements PaymentProcessingService {

    @Autowired
    private ZeebeClient zeebeClient;

    @Override
    public void processPayment(SubscriptionBasedPayment subscriptionBasedPayment) {
        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("subscription_process_1")
                .latestVersion()
                .variable("subscription",subscriptionBasedPayment)
                .send()
                .join();

        log.info("Processing subscription based payment ID {} with Camunda", subscriptionBasedPayment.getId());
    }
}
