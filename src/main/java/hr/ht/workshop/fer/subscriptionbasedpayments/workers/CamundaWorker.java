package hr.ht.workshop.fer.subscriptionbasedpayments.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

public interface CamundaWorker {

    void handleJob(JobClient client, ActivatedJob job);
}
