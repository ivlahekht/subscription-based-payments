package hr.ht.workshop.fer.subscriptionbasedpayments.config;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;

@Configuration
@Profile(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
public class ZeebeConfig {

    @Value("${zeebe.broker.contactPoint}")
    private String contactPoint;

    @Bean
    public ZeebeClient zeebeClient() {
        return ZeebeClient.newClientBuilder()
                .grpcAddress(URI.create(contactPoint))
                .usePlaintext()
                .build();
    }
}
