package hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentGatewayEndpoints {

    public static final String DEDUCT_MONEY_V1 = "/v1/deductMoney";
}
