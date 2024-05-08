package hr.ht.workshop.fer.subscriptionbasedpayments.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Profile("!test")
@Configuration
public class WireMockConfig {

    @Value("${wiremock.server.port}")
    private int wiremockServerPort;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wiremockServer() {
        WireMockServer server = new WireMockServer(
                WireMockConfiguration
                        .wireMockConfig()
                        .port(wiremockServerPort)
                        .usingFilesUnderDirectory("." + System.getProperty("file.separator") + "external"));
        server.start();
        return server;
    }
}
