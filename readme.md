# Fer Camunda Workshop
```
https://github.com/ivlahekht/subscription-based-payments
```
## Part 1 Showcase of a naive approach

```
checkout naive-approach branch
```

### Introduction

In this part we will show you there are 2 ways to approach this problem:
- naive approach
- camunda approach

We will showcase the benefits of Camunda using a subscription-based model.

This model is well known to people paying for subscription-based services like Netflix and Disney+.

In the naive approach, we will not use:
- any of the technologies on the market
- any of the modelling tools (we will use UML to explain what is happening)

In the following picture, you can find the subscription-based model modelled in UML.

<img src="naive_approach_uml.png" width="900"/>

### Application walkthrough

The application implements the subscription-based model.
Each call on the diagram is represented by the class in the project:
- schedule payment for the next month -> PaymentSchedulingService
- charge the user -> PaymentGatewayService
- notify the user -> NotificationService
- activate the grace period -> ProductService
- suspend the service -> ProductService
- orchestration of the process -> NaivePaymentProcessingService

In the uml, we have 3 external service calls:
- Notification Service
- Payment Service
- Product Service

We will use Wiremock which will act as a server for the external calls we use.

The process is triggered by sending a POST on http://localhost:8080/v1/subscriptionBasedPayment

## Demo

You will need to run the application with the profile *naive-implementation*

We have prepared two requests to show 2 possible paths in the process:
- successful payment
```jsx
{
    "subscriberId":1,
        "productId":456287,
        "subscriptionDurationUnit":"SECONDS",
        "subscriptionDurationAmount": 15, /*2592000 = 30 dana*/
        "price": 15.00
}
```
- unsuccessful payment
```jsx
{
    "subscriberId":10297734098,
    "productId":456287,
    "subscriptionDurationUnit":"SECONDS",
    "subscriptionDurationAmount": 10, /*2592000 = 30 dana*/
    "price": 1562.00
}
```

Try and execute the process by sending both requests.

What are the downsides of this approach?

