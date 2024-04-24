package hr.ht.workshop.fer.subscriptionbasedpayments.client.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductEndpoints {

    public static final String ACTIVATE_GRACE_PERIOD_V1 = "/v1/activateGracePeriod";
    public static final String SUSPEND_V1 = "/v1/suspend";
}
