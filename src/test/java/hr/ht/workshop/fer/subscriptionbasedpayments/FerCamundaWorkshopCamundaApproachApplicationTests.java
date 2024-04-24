package hr.ht.workshop.fer.subscriptionbasedpayments;

import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.CAMUNDA_BASED_IMPLEMENTATION)
class FerCamundaWorkshopCamundaApproachApplicationTests extends AbstractWiremockApplicationTest {

    @Test
    @DisplayName("Should spin up the Camunda approach application successfully")
    void contextLoads() {  }
}
