package hr.ht.workshop.fer.subscriptionbasedpayments.exception;

public class PaymentUnsuccessfulOnPaymentGatewayException extends RuntimeException {

    public PaymentUnsuccessfulOnPaymentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
