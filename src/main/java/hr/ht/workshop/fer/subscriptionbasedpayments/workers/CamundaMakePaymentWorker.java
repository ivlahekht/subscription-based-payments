package hr.ht.workshop.fer.subscriptionbasedpayments.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import hr.ht.workshop.fer.subscriptionbasedpayments.exception.PaymentUnsuccessfulOnPaymentGatewayException;
import hr.ht.workshop.fer.subscriptionbasedpayments.service.PaymentGatewayService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
public class CamundaMakePaymentWorker implements CamundaWorker{

    private final PaymentGatewayService paymentGatewayService;

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void subscribeToJobType() {
        zeebeClient.newWorker()
                .jobType("make-payment-service")
                .handler(this::handleJob)
                .open();
    }

    @Override
    public void handleJob(JobClient client, ActivatedJob job) {
        try {
            Map<String, Object> subscriptionMap = job.getVariablesAsMap();
            SubscriptionBasedPayment subscription = objectMapper.convertValue(subscriptionMap, SubscriptionBasedPayment.class);
            paymentGatewayService.processPaymentOnPaymentGateway(subscription);
            client.newCompleteCommand(job.getKey())
                    .send();
        } catch(PaymentUnsuccessfulOnPaymentGatewayException e){
            client.newThrowErrorCommand(job.getKey())
                    .errorCode("PAYMENT_FAILED")
                    .errorMessage(e.getMessage())
                    .send();
        }
    }
}
