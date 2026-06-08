package guru.springframework.spring6reactive.controllers;

import guru.springframework.spring6reactive.domain.Beer;
import guru.springframework.spring6reactive.model.BeerDTO;
import guru.springframework.spring6reactive.repositories.BeerRepositoryTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
        "logging.level.org.springframework.security=DEBUG"
})
class BeerControllerTest {

    // Note: @Autowired did NOT work correctly here, it never took the security context into account,
    // so we had to build the WebTestClient manually in the test methods. See setUp() method for details.
    WebTestClient webTestClient;

    @Autowired
    ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .apply(springSecurity())
                .configureClient()
                .build();
    }

    @Test
    void testPatchIdNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .patch()
                .uri(BeerController.BEER_PATH_ID, 999)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .delete()
                .uri(BeerController.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(999)
    void testDeleteBeer() {
        webTestClient.mutateWith(mockOAuth2Login())
                .delete()
                .uri(BeerController.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(4)
    void testUpdateBeerBadRequest() {
        Beer testBeer = BeerRepositoryTest.getTestBeer();
        testBeer.setBeerStyle("");

        webTestClient.mutateWith(mockOAuth2Login())
                .put()
                .uri(BeerController.BEER_PATH_ID, 1)
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateBeerNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .put()
                .uri(BeerController.BEER_PATH_ID, 999)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void testUpdateBeer() {
        webTestClient.mutateWith(mockOAuth2Login())
                .put()
                .uri(BeerController.BEER_PATH_ID, 1)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testCreateBeerBadData() {
        Beer testBeer = BeerRepositoryTest.getTestBeer();
        testBeer.setBeerName("");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(BeerController.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateBeer() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(BeerController.BEER_PATH)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v2/beer/4");
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(BeerController.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(1)
    void testGetById() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(BeerController.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(2)
    void testListBeers() {
        webTestClient
                //.mutateWith(mockJwt())
                .mutateWith(mockJwt().jwt(jwt -> 
                    jwt.claim("scope", "message.read")
                    .claim("scope", "message.write")
                    .subject("Michael")))
                //.mutateWith(mockOAuth2Login())
                .get().uri(BeerController.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    void testTemp() {
        webTestClient
            //.mutateWith(mockJwt())
            .mutateWith(mockJwt().jwt(jwt -> jwt.subject("michael")))
            .get()
            .uri("/debug")
            .exchange()
            .expectBody(String.class)
            .consumeWith(System.out::println);
    }
}
