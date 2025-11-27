package com.petfriends.transporte.clients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AlmoxarifadoClient")
class AlmoxarifadoClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private AlmoxarifadoClient client;

    @BeforeEach
    void setUp() {
        client = new AlmoxarifadoClient(webClient);
    }

    private void setupWebClientMocks() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("Deve consultar reserva com sucesso")
    void deveConsultarReservaComSucesso() {
        setupWebClientMocks();
        
        AlmoxarifadoClient.ReservaEstoqueDTO dto = new AlmoxarifadoClient.ReservaEstoqueDTO();
        dto.setId("RES-001");
        dto.setPedidoId("PED-001");
        dto.setStatus("SEPARADA");
        dto.setOperadorId("OP-001");

        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(client.consultarReserva("RES-001"))
                .assertNext(reserva -> {
                    assertEquals("RES-001", reserva.getId());
                    assertEquals("PED-001", reserva.getPedidoId());
                    assertEquals("SEPARADA", reserva.getStatus());
                    assertEquals("OP-001", reserva.getOperadorId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar empty quando reserva não existe")
    void deveRetornarEmptyQuandoReservaNaoExiste() {
        setupWebClientMocks();
        
        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        StepVerifier.create(client.consultarReserva("RES-999"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve tratar erro de comunicação")
    void deveTratarErroDeComunicacao() {
        setupWebClientMocks();
        
        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        StepVerifier.create(client.consultarReserva("RES-001"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve consultar reserva por pedido com sucesso")
    void deveConsultarReservaPorPedidoComSucesso() {
        setupWebClientMocks();
        
        AlmoxarifadoClient.ReservaEstoqueDTO dto = new AlmoxarifadoClient.ReservaEstoqueDTO();
        dto.setId("RES-001");
        dto.setPedidoId("PED-001");
        dto.setStatus("CONFIRMADA");

        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(client.consultarReservaPorPedido("PED-001"))
                .assertNext(reserva -> {
                    assertEquals("RES-001", reserva.getId());
                    assertEquals("PED-001", reserva.getPedidoId());
                    assertEquals("CONFIRMADA", reserva.getStatus());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar empty quando consultar por pedido inexistente")
    void deveRetornarEmptyQuandoConsultarPorPedidoInexistente() {
        setupWebClientMocks();
        
        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        StepVerifier.create(client.consultarReservaPorPedido("PED-999"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve verificar se reserva está pronta para entrega - status SEPARADA")
    void deveVerificarSeReservaEstaProntaParaEntrega() {
        setupWebClientMocks();
        
        AlmoxarifadoClient.ReservaEstoqueDTO dto = new AlmoxarifadoClient.ReservaEstoqueDTO();
        dto.setId("RES-001");
        dto.setPedidoId("PED-001");
        dto.setStatus("SEPARADA");

        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(client.reservaProntaParaEntrega("RES-001"))
                .assertNext(pronta -> assertTrue(pronta))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar false quando reserva não está separada")
    void deveRetornarFalseQuandoReservaNaoEstaSeparada() {
        setupWebClientMocks();
        
        AlmoxarifadoClient.ReservaEstoqueDTO dto = new AlmoxarifadoClient.ReservaEstoqueDTO();
        dto.setId("RES-001");
        dto.setPedidoId("PED-001");
        dto.setStatus("CONFIRMADA");

        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(client.reservaProntaParaEntrega("RES-001"))
                .assertNext(pronta -> assertFalse(pronta))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar false quando reserva não existe")
    void deveRetornarFalseQuandoReservaNaoExiste() {
        setupWebClientMocks();
        
        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        StepVerifier.create(client.reservaProntaParaEntrega("RES-999"))
                .assertNext(pronta -> assertFalse(pronta))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve criar DTO com todos os campos")
    void deveCriarDTOComTodosCampos() {
        AlmoxarifadoClient.ReservaEstoqueDTO dto = new AlmoxarifadoClient.ReservaEstoqueDTO();
        dto.setId("RES-001");
        dto.setPedidoId("PED-001");
        dto.setStatus("SEPARADA");
        dto.setOperadorId("OP-001");

        assertEquals("RES-001", dto.getId());
        assertEquals("PED-001", dto.getPedidoId());
        assertEquals("SEPARADA", dto.getStatus());
        assertEquals("OP-001", dto.getOperadorId());
    }

    @Test
    @DisplayName("Deve verificar status PENDENTE retorna false")
    void deveVerificarStatusPendenteRetornaFalse() {
        setupWebClientMocks();
        
        AlmoxarifadoClient.ReservaEstoqueDTO dto = new AlmoxarifadoClient.ReservaEstoqueDTO();
        dto.setId("RES-001");
        dto.setStatus("PENDENTE");

        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(client.reservaProntaParaEntrega("RES-001"))
                .assertNext(pronta -> assertFalse(pronta))
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve verificar status CANCELADA retorna false")
    void deveVerificarStatusCanceladaRetornaFalse() {
        setupWebClientMocks();
        
        AlmoxarifadoClient.ReservaEstoqueDTO dto = new AlmoxarifadoClient.ReservaEstoqueDTO();
        dto.setId("RES-003");
        dto.setStatus("CANCELADA");

        when(responseSpec.bodyToMono(AlmoxarifadoClient.ReservaEstoqueDTO.class))
                .thenReturn(Mono.just(dto));

        StepVerifier.create(client.reservaProntaParaEntrega("RES-003"))
                .assertNext(pronta -> assertFalse(pronta))
                .verifyComplete();
    }
}

