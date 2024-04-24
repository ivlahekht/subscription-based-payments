package hr.ht.workshop.fer.subscriptionbasedpayments.client.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "products", url = "${client.product.url}")
public interface ProductClient {

    @PostMapping(ProductEndpoints.ACTIVATE_GRACE_PERIOD_V1)
    void activateGracePeriod(@RequestParam(ProductQueryParams.PRODUCT_ID_QUERY_PARAM) Long productId);

    @PostMapping(ProductEndpoints.SUSPEND_V1)
    void suspend(@RequestParam(ProductQueryParams.PRODUCT_ID_QUERY_PARAM) Long productId);
}
