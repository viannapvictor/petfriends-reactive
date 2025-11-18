package com.petfriends.transporte.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI transporteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetFriends - API de Transporte")
                        .description("""
                                API para gestão de entregas e expedição do sistema PetFriends.
                                
                                **Arquitetura:** DDD + CQRS + Event Sourcing
                                
                                **Bounded Context:** Transporte
                                
                                **Responsabilidades:**
                                - Agendar entregas
                                - Iniciar transporte
                                - Concluir entregas
                                - Consultar status de entregas
                                - Rastreamento de pedidos
                                """)
                        .version("1.0.0-SNAPSHOT")
                        .contact(new Contact()
                                .name("Equipe PetFriends")
                                .email("contato@petfriends.com")
                                .url("https://petfriends.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor Local - Desenvolvimento")
                ));
    }
}

