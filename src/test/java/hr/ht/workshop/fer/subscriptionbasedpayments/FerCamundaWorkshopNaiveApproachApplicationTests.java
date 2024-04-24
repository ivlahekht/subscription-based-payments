package hr.ht.workshop.fer.subscriptionbasedpayments;

import hr.ht.workshop.fer.subscriptionbasedpayments.config.Profiles;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.NAIVE_IMPLEMENTATION)
class FerCamundaWorkshopNaiveApproachApplicationTests extends AbstractWiremockApplicationTest {

    @Test
    @DisplayName("Should spin up the naive approach application successfully")
    void contextLoads() { }
}
