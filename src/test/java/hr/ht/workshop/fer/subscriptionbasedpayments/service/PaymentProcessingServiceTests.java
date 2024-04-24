package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import hr.ht.workshop.fer.subscriptionbasedpayments.AbstractWiremockApplicationTest;
import hr.ht.workshop.fer.subscriptionbasedpayments.client.notification.NotificationEndpoints;
import hr.ht.workshop.fer.subscriptionbasedpayments.client.notification.NotificationQueryParams;
import hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway.PaymentGatewayEndpoints;
import hr.ht.workshop.fer.subscriptionbasedpayments.client.paymentgateway.PaymentGatewayQueryParams;
import hr.ht.workshop.fer.subscriptionbasedpayments.client.product.ProductEndpoints;
import hr.ht.workshop.fer.subscriptionbasedpayments.client.product.ProductQueryParams;
import hr.ht.workshop.fer.subscriptionbasedpayments.dto.CreateSubscriptionBasedPaymentDto;
import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static hr.ht.workshop.fer.subscriptionbasedpayments.controller.definition.Endpoints.SUBSCRIPTION_BASED_PAYMENTS_V1;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
abstract class PaymentProcessingServiceTests extends AbstractWiremockApplicationTest {

    private static final String SUBSCRIPTION_DURATION_UNIT_SECONDS = "SECONDS";
    private static final String FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO = "FIRST_PAYMENT_UNSUCCESSFUL_SCENARIO";
    private static final String FIRST_PAYMENT_ATTEMPTED_SCENARIO_STEP = "FIRST_PAYMENT_SCENARIO_ATTEMPTED_STEP";
    private static final String PAYMENT_AFTER_GRACE_PERIOD_SUCCESSFUL_SCENARIO_STEP = "PAYMENT_AFTER_GRACE_PERIOD_UNSUCCESSFUL_STEP";
    private static final String PAYMENT_AFTER_GRACE_PERIOD_UNSUCCESSFUL_SCENARIO_STEP = "PAYMENT_AFTER_GRACE_PERIOD_UNSUCCESSFUL_STEP";

    private static final int GRACE_PERIOD_DURATION_SECONDS = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should schedule and execute recurring payments")
    void should_schedule_and_execute_recurring_payments() {
        // PREPARE THE TEST DATA
        long subscriberId = 1L, productId = 1L, subscriptionDurationAmountSeconds = 2L;
        double price = 15.0;

        int expectedNumberOfPaymentGatewayInvocations = 2;

        // EXECUTE THE TEST
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .withQueryParam(PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                .withQueryParam(PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM, WireMock.equalTo(String.valueOf(price)))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        submitSubscriptionBasedPaymentInSecondsCreation(subscriberId, productId, subscriptionDurationAmountSeconds, price);

        // ASSERT THE TEST DATA
        Duration minimalWaitingDuration =
                Duration.of(2 * subscriptionDurationAmountSeconds, ChronoUnit.valueOf(SUBSCRIPTION_DURATION_UNIT_SECONDS));

        Awaitility.await()
                .atLeast(minimalWaitingDuration)
                .atMost(minimalWaitingDuration.plusSeconds(1))
                .untilAsserted(() -> WireMock.verify(expectedNumberOfPaymentGatewayInvocations, WireMock.postRequestedFor(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                        .withQueryParam(PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                        .withQueryParam(PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM, WireMock.equalTo(String.valueOf(price)))));
    }

    @Test
    @DisplayName("Should notify the subscriber after unsuccessful payment")
    void should_notify_the_subscriber_after_unsuccessful_payment() {
        // PREPARE THE TEST DATA
        long subscriberId = 1L, productId = 1L, subscriptionDurationAmountSeconds = 2L;
        double price = 15.0;

        String expectedNotificationMessage = String.format(
                "Payment for the product %s unsuccessful. We could not deduct %.2f euros from your account. Please verify your payment method data.", productId, price);

        // EXECUTE THE TEST
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(NotificationEndpoints.NOTIFICATION_V1))
                .withQueryParam(NotificationQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                .withQueryParam(NotificationQueryParams.MESSAGE_QUERY_PARAM, WireMock.equalTo(expectedNotificationMessage))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        submitSubscriptionBasedPaymentInSecondsCreation(subscriberId, productId, subscriptionDurationAmountSeconds, price);

        // ASSERT THE TEST DATA
        Duration minimalWaitingDuration =
                Duration.of(subscriptionDurationAmountSeconds, ChronoUnit.valueOf(SUBSCRIPTION_DURATION_UNIT_SECONDS));

        Awaitility.await()
                .atLeast(minimalWaitingDuration)
                .atMost(minimalWaitingDuration.plusSeconds(1))
                .untilAsserted(() -> WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo(NotificationEndpoints.NOTIFICATION_V1))
                        .withQueryParam(NotificationQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                        .withQueryParam(NotificationQueryParams.MESSAGE_QUERY_PARAM, WireMock.equalTo(expectedNotificationMessage))));
    }

    @Test
    @DisplayName("Should activate grace period after unsuccessful payment")
    void should_activate_grace_period_after_unsuccessful_payment() {
        // PREPARE THE TEST DATA
        long subscriberId = 1L, productId = 1L, subscriptionDurationAmountSeconds = 2L;
        double price = 15.0;

        // EXECUTE THE TEST
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(NotificationEndpoints.NOTIFICATION_V1))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(ProductEndpoints.ACTIVATE_GRACE_PERIOD_V1))
                .withQueryParam(ProductQueryParams.PRODUCT_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(productId)))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        submitSubscriptionBasedPaymentInSecondsCreation(subscriberId, productId, subscriptionDurationAmountSeconds, price);

        // ASSERT THE TEST DATA
        Duration minimalWaitingDuration =
                Duration.of(subscriptionDurationAmountSeconds, ChronoUnit.valueOf(SUBSCRIPTION_DURATION_UNIT_SECONDS));

        Awaitility.await()
                .atLeast(minimalWaitingDuration)
                .atMost(minimalWaitingDuration.plusSeconds(1))
                .untilAsserted(() -> WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo(ProductEndpoints.ACTIVATE_GRACE_PERIOD_V1))
                        .withQueryParam(ProductQueryParams.PRODUCT_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(productId)))));
    }

    @Test
    @DisplayName("Should retry the payment after the grace period ends")
    void should_retry_payment_after_grace_period_ends() {
        // PREPARE THE TEST DATA
        long subscriberId = 1L, productId = 1L, subscriptionDurationAmountSeconds = 2L;
        double price = 15.0;

        int expectedNumberOfPaymentGatewayInvocations = 2;

        // EXECUTE THE TEST
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .withQueryParam(PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                .withQueryParam(PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM, WireMock.equalTo(String.valueOf(price)))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value()))
                .willSetStateTo(FIRST_PAYMENT_ATTEMPTED_SCENARIO_STEP));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(NotificationEndpoints.NOTIFICATION_V1))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(ProductEndpoints.ACTIVATE_GRACE_PERIOD_V1))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .withQueryParam(PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                .withQueryParam(PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM, WireMock.equalTo(String.valueOf(price)))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(FIRST_PAYMENT_ATTEMPTED_SCENARIO_STEP)
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        submitSubscriptionBasedPaymentInSecondsCreation(subscriberId, productId, subscriptionDurationAmountSeconds, price);

        // ASSERT THE TEST DATA
        Duration minimalWaitingDuration =
                Duration.of(subscriptionDurationAmountSeconds + GRACE_PERIOD_DURATION_SECONDS, ChronoUnit.valueOf(SUBSCRIPTION_DURATION_UNIT_SECONDS));

        Awaitility.await()
                .atLeast(minimalWaitingDuration)
                .atMost(minimalWaitingDuration.plusSeconds(1))
                .untilAsserted(() -> WireMock.verify(expectedNumberOfPaymentGatewayInvocations, WireMock.postRequestedFor(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                        .withQueryParam(PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                        .withQueryParam(PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM, WireMock.equalTo(String.valueOf(price)))));
    }

    @Test
    @DisplayName("Should resume executing recurring payments after successful payment during the grace period")
    void should_resume_executing_recurring_payments_after_successful_payment_during_the_grace_period() {
        // PREPARE THE TEST DATA
        long subscriberId = 1L, productId = 1L, subscriptionDurationAmountSeconds = 2L;
        double price = 15.0;

        int expectedNumberOfPaymentGatewayInvocations = 3;

        // EXECUTE THE TEST
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value()))
                .willSetStateTo(FIRST_PAYMENT_ATTEMPTED_SCENARIO_STEP));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(NotificationEndpoints.NOTIFICATION_V1))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(ProductEndpoints.ACTIVATE_GRACE_PERIOD_V1))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(FIRST_PAYMENT_ATTEMPTED_SCENARIO_STEP)
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson())
                .willSetStateTo(PAYMENT_AFTER_GRACE_PERIOD_SUCCESSFUL_SCENARIO_STEP));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .withQueryParam(PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                .withQueryParam(PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM, WireMock.equalTo(String.valueOf(price)))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(PAYMENT_AFTER_GRACE_PERIOD_SUCCESSFUL_SCENARIO_STEP)
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        submitSubscriptionBasedPaymentInSecondsCreation(subscriberId, productId, subscriptionDurationAmountSeconds, price);

        // ASSERT THE TEST DATA
        Duration minimalWaitingDuration =
                Duration.of(2 * subscriptionDurationAmountSeconds + GRACE_PERIOD_DURATION_SECONDS, ChronoUnit.valueOf(SUBSCRIPTION_DURATION_UNIT_SECONDS));

        Awaitility.await()
                .atLeast(minimalWaitingDuration)
                .atMost(minimalWaitingDuration.plusSeconds(1))
                .untilAsserted(() -> WireMock.verify(expectedNumberOfPaymentGatewayInvocations, WireMock.postRequestedFor(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                        .withQueryParam(PaymentGatewayQueryParams.SUBSCRIBER_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(subscriberId)))
                        .withQueryParam(PaymentGatewayQueryParams.AMOUNT_QUERY_PARAM, WireMock.equalTo(String.valueOf(price)))));
    }

    @Test
    @DisplayName("Should suspend the product after unsuccessful payment during the grace period")
    void should_suspend_the_product_after_unsuccessful_payment_during_the_grace_period() {
        // PREPARE THE TEST DATA
        long subscriberId = 1L, productId = 1L, subscriptionDurationAmountSeconds = 2L;
        double price = 15.0;

        // EXECUTE THE TEST
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value()))
                .willSetStateTo(FIRST_PAYMENT_ATTEMPTED_SCENARIO_STEP));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(NotificationEndpoints.NOTIFICATION_V1))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(ProductEndpoints.ACTIVATE_GRACE_PERIOD_V1))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(FIRST_PAYMENT_ATTEMPTED_SCENARIO_STEP)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value()))
                .willSetStateTo(PAYMENT_AFTER_GRACE_PERIOD_UNSUCCESSFUL_SCENARIO_STEP));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(PaymentGatewayEndpoints.DEDUCT_MONEY_V1))
                .inScenario(FIRST_PAYMENT_UNSUCCESSFUL_PAYMENT_GATEWAY_SCENARIO)
                .whenScenarioStateIs(PAYMENT_AFTER_GRACE_PERIOD_UNSUCCESSFUL_SCENARIO_STEP)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(ProductEndpoints.SUSPEND_V1))
                .withQueryParam(ProductQueryParams.PRODUCT_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(productId)))
                .willReturn(ResponseDefinitionBuilder.okForEmptyJson()));

        submitSubscriptionBasedPaymentInSecondsCreation(subscriberId, productId, subscriptionDurationAmountSeconds, price);

        // ASSERT THE TEST DATA
        Duration minimalWaitingDuration =
                Duration.of(subscriptionDurationAmountSeconds + GRACE_PERIOD_DURATION_SECONDS, ChronoUnit.valueOf(SUBSCRIPTION_DURATION_UNIT_SECONDS));

        Awaitility.await()
                .atLeast(minimalWaitingDuration)
                .atMost(minimalWaitingDuration.plusSeconds(1))
                .untilAsserted(() -> WireMock.verify(WireMock.postRequestedFor(WireMock.urlPathEqualTo(ProductEndpoints.SUSPEND_V1))
                        .withQueryParam(ProductQueryParams.PRODUCT_ID_QUERY_PARAM, WireMock.equalTo(String.valueOf(productId)))));
    }

    @SneakyThrows
    private void submitSubscriptionBasedPaymentInSecondsCreation(long subscriberId, long productId, long durationAmount, double price) {
        CreateSubscriptionBasedPaymentDto createSubscriptionBasedPaymentDto = CreateSubscriptionBasedPaymentDto.builder()
                .subscriberId(subscriberId)
                .productId(productId)
                .subscriptionDurationAmount(durationAmount)
                .subscriptionDurationUnit(SUBSCRIPTION_DURATION_UNIT_SECONDS)
                .price(price)
                .build();

        mockMvc.perform(post(SUBSCRIPTION_BASED_PAYMENTS_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSubscriptionBasedPaymentDto)))
                .andExpect(status().is(ACCEPTED.value()));
    }
}
