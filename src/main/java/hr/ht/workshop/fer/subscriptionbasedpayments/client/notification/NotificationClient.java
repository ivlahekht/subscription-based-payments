package hr.ht.workshop.fer.subscriptionbasedpayments.client.notification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static hr.ht.workshop.fer.subscriptionbasedpayments.client.notification.NotificationQueryParams.MESSAGE_QUERY_PARAM;
import static hr.ht.workshop.fer.subscriptionbasedpayments.client.notification.NotificationQueryParams.SUBSCRIBER_ID_QUERY_PARAM;

@FeignClient(name = "notifications", url = "${client.notification.url}")
public interface NotificationClient {

    @PostMapping(NotificationEndpoints.NOTIFICATION_V1)
    void createNotification(@RequestParam(SUBSCRIBER_ID_QUERY_PARAM) Long subscriberId, @RequestParam(MESSAGE_QUERY_PARAM) String message);
}
