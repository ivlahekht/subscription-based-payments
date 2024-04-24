package hr.ht.workshop.fer.subscriptionbasedpayments.client.notification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationQueryParams {

    public static final String SUBSCRIBER_ID_QUERY_PARAM = "subscriberId";
    public static final String MESSAGE_QUERY_PARAM = "message";
}
