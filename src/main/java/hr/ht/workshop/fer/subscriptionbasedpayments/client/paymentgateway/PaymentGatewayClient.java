package hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway.PaymentGatewayEndpoints.DEDUCT_MONEY_V1;
import static hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway.PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM;
import static hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway.PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM;

@FeignClient(name = "paymentGateway", url = "${client.payment-gateway.url}")
public interface PaymentGatewayClient {

    @PostMapping(DEDUCT_MONEY_V1)
    void deductMoney(@RequestParam(SUBSCRIBER_ID_QUERY_PARAM) Long subscriberId, @RequestParam(AMOUNT_QUERY_PARAM) Double amount);
}
