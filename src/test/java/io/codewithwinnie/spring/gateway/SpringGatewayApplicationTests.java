package io.codewithwinnie.spring.gateway;

import io.codewithwinnie.spring.gateway.config.SpringGatewayConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
properties = {
		"httpBin=http://localhost:${wiremock.server.port}"
})
@AutoConfigureWireMock(port = 0)
@ContextConfiguration(classes = {
		SpringGatewayConfig.class,
		SpringGatewayApplication.class
})
class SpringGatewayApplicationTests {

	@Autowired
	private ServerProperties serverProperties;

	@Autowired
	private SpringGatewayApplication application;

	@Autowired
	private WebTestClient webTestClient;



	@Test
	void contextLoads() {
		assertThat(application).isNotNull();
	}

	@Test
	void testGatewayUri() {

		stubFor(get(urlEqualTo("/get"))
				    .willReturn(aResponse().withBody("{\"headers\": {\"hello\": \"world\"}}")
											.withHeader("Content-Type", "application/json")
								)
		);

		stubFor(get(urlEqualTo("/delay/3"))
				.willReturn(aResponse().withBody("no fallback")
						.withFixedDelay(3000)
				)
		);

		webTestClient.get().uri("/get")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.headers.hello").isEqualTo("world");


		webTestClient.get().uri("/delay/3")
				.header("Host", "www.circuitbreaker.com")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.consumeWith(response ->
						assertThat(response.getResponseBody()).contains("fallback".getBytes()));

	}

}
