package hr.ht.workshop.fer.subscriptionbasedpayments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Profile(Profiles.NAIVE_IMPLEMENTATION)
public class NaiveAsynchronicityConfiguration { }
