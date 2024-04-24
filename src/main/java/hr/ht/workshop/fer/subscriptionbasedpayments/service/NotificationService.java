package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.client.notification.NotificationClient;
import hr.ht.workshop.fer.subscriptionbasedpayments.entity.SubscriptionBasedPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationService {

    private final NotificationClient notificationClient;

    public void notifySubscriberAboutUnsuccessfulPayment(SubscriptionBasedPayment subscriptionBasedPayment) {
        log.info("Notifying subscriber ID {} about subscription based payment ID {} unsuccessful payment",
                subscriptionBasedPayment.getSubscriberId(), subscriptionBasedPayment.getId());
        String subscriberNotification = prepareNotificationAboutUnsuccessfulPayment(subscriptionBasedPayment);
        notificationClient.createNotification(subscriptionBasedPayment.getSubscriberId(), subscriberNotification);
        log.info("Subscriber ID {} successfully notified about the unsuccessful payment", subscriptionBasedPayment.getSubscriberId());
    }

    private String prepareNotificationAboutUnsuccessfulPayment(SubscriptionBasedPayment subscriptionBasedPayment) {
        log.info("Preparing notification about unsuccessful payment for the subscriber ID {}", subscriptionBasedPayment.getSubscriberId());
        String subscriberNotification = String.format("Payment for the product %s unsuccessful. We could not deduct %.2f euros from your account. Please verify your payment method data.",
                subscriptionBasedPayment.getProductId(), subscriptionBasedPayment.getPrice());
        log.info("Notification content prepared. It is: {}", subscriberNotification);
        return subscriberNotification;
    }
}
