package com.bidding.system.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Inicializa a aplicação Spring Boot para o frontend
// @SpringBootApplication engloba @Configuration, @EnableAutoConfiguration e @ComponentScan
@SpringBootApplication
public class BiddingFrontendApplication {

    public static void main(String[] args) {
        // Ponto de entrada da aplicação Java: inicia o contexto Spring e o servidor Tomcat embutido
        SpringApplication.run(BiddingFrontendApplication.class, args);
    }

}