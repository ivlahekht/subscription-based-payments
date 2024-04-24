package hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentGatewayQueryParams {

    public static final String SUBSCRIBER_ID_QUERY_PARAM = "subscriberId";
    public static final String AMOUNT_QUERY_PARAM = "amount";
}
