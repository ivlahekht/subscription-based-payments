package hr.ht.workshop.fer.subscriptionbasedpayments.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import hr.ht.workshop.fer.subscriptionbasedpayments.service.ProductService;
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
public class CamundaActivateGracePeriodWorker implements CamundaWorker{

    private final ProductService productService;

    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void subscribeToJobType() {
        zeebeClient.newWorker()
                .jobType("activate-grace-period-service")
                .handler(this::handleJob)
                .open();
    }
    @Override
    public void handleJob(JobClient client, ActivatedJob job) {
        Map<String, Object> subscriptionMap = job.getVariablesAsMap();
        SubscriptionBasedPayment subscription = objectMapper.convertValue(subscriptionMap, SubscriptionBasedPayment.class);
        productService.activateGracePeriod(subscription.getProductId());
        client.newCompleteCommand(job.getKey())
                .send();
    }
}
