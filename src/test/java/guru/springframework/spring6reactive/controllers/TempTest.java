package guru.springframework.spring6reactive.controllers;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class TempTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void contextCheck() {
        System.out.println(webTestClient);
        System.out.println("Reactive? " + applicationContext.containsBean("webHandler"));
        System.out.println("MVC? " + applicationContext.containsBean("dispatcherServlet"));
        System.out.println("Main class: " + applicationContext.getBeansWithAnnotation(SpringBootApplication.class));
        System.out.println("Has Netty server? " + applicationContext.containsBean("reactorHttpServer"));
        System.out.println("Has WebFluxAutoConfiguration? " +
            Arrays.stream(applicationContext.getBeanDefinitionNames())
                .anyMatch(n -> n.contains("WebFluxAutoConfiguration")));
        System.out.println("Has HttpHandler? " + applicationContext.containsBean("webHandler"));
        System.out.println("Has WebFluxConfig? " + applicationContext.containsBean("webFluxConfig"));
        System.out.println("Has RouterFunction? " +
            Arrays.stream(applicationContext.getBeanDefinitionNames())
                .anyMatch(n -> n.toLowerCase().contains("router")));

    }
}
