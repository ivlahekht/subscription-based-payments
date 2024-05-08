package hr.ht.workshop.fer.subscriptionbasedpayments.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import hr.ht.workshop.fer.subscriptionbasedpayments.service.ProductService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
@Slf4j
public class CamundaSimpleServiceTask implements CamundaWorker {

    @Autowired
    private ZeebeClient zeebeClient;


    @PostConstruct
    public void subscribeToJobType() {
        zeebeClient.newWorker()
                .jobType("camunda-simple-service-task")
                .handler(this::handleJob)
                .open();
    }

    @Override
    public void handleJob(JobClient client, ActivatedJob job) {
        log.info("Camunda simple service worker activated for job with key: {}", job.getKey());
        Map<String, Object> variables = job.getVariablesAsMap();
        Integer duration = (Integer) variables.get("duration");
        variables.put("waitUntil", LocalDateTime
                .now()
                .plusSeconds(duration)
                .atOffset(ZoneOffset.of("+02:00"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send();
    }
}
