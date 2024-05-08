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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
@Slf4j
public class CamundaMakePaymentWorker implements CamundaWorker {

    private final PaymentGatewayService paymentGatewayService;

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void subscribeToJobType() {
        String jobName = "make-payment-service";
        log.info("Creating {} job!", jobName);
        zeebeClient.newWorker()
                .jobType(jobName)
                .handler(this::handleJob)
                .open();
    }

    @Override
    public void handleJob(JobClient client, ActivatedJob job) {
        try {
            log.info("Processing payment on gateway!");

            log.info("New complete command sent by the client!");
        } catch (PaymentUnsuccessfulOnPaymentGatewayException e) {
            log.error("Error occurred during processing payment on gateway", e);
            client.newThrowErrorCommand(job.getKey())
                    .errorCode("PAYMENT_FAILED")
                    .errorMessage(e.getMessage())
                    .send();
        }
    }
}
