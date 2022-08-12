package io.codewithwinnie.spring.gateway.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringGatewayConfig {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder routeLocatorBuilder, UriConfiguration configuration) {
        String uri = configuration.getHttpBin();
        return routeLocatorBuilder.routes()
                .route(r ->
                        r.path("/get")
                                .filters(f -> f.addRequestHeader("hello", "world"))
                                .uri(uri))
                .route(r -> r.host("*.circuitbreaker.com")
                        .filters(f -> f.circuitBreaker(config -> config.setName("myConfig").setFallbackUri("forward:/fallback")))
                        .uri(uri))
                .build();
    }

    @Bean
    public UriConfiguration configuration() {
        return new UriConfiguration();
    }
}
