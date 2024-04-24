package hr.ht.workshop.fer.subscriptionbasedpayments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FerCamundaWorkshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(FerCamundaWorkshopApplication.class, args);
    }

}
