package hr.ht.workshop.fer.subscriptionbasedpayments.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Profiles {

    public static final String CAMUNDA_BASED_IMPLEMENTATION = "camunda-based-implementation";
    public static final String NAIVE_IMPLEMENTATION = "naive-implementation";
}
