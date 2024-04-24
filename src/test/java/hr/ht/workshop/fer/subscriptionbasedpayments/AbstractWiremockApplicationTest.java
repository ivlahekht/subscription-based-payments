package hr.ht.workshop.fer.subscriptionbasedpayments;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
public abstract class AbstractWiremockApplicationTest { }