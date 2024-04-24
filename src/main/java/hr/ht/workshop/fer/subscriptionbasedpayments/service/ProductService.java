package hr.ht.workshop.fer.subscriptionbasedpayments.service;

import hr.ht.workshop.fer.subscriptionbasedpayments.client.product.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductService {

    private final ProductClient productClient;

    public void activateGracePeriod(Long productId) {
        log.info("Activating grace period for the product ID {}", productId);
        productClient.activateGracePeriod(productId);
        log.info("Grace period has been successfully activated for the product ID {}", productId);
    }

    public void suspendProduct(Long productId) {
        log.info("Suspending product ID {}", productId);
        productClient.suspend(productId);
        log.info("Product ID {} suspension successful", productId);
    }
}
