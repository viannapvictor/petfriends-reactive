package com.petfriends.transporte.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petfriends.transporte.domain.Endereco;
import com.petfriends.transporte.events.*;
import com.petfriends.transporte.events.almoxarifado.ItensSeparados;
import com.petfriends.transporte.readmodel.EntregaView;
import com.petfriends.transporte.readmodel.EntregaViewRepository;
import com.petfriends.transporte.services.EntregaCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EntregaProjection")
class EntregaProjectionTest {

    @Mock
    private EntregaViewRepository viewRepository;

    @Mock
    private EntregaCommandService commandService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private EntregaProjection projection;

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        projection = new EntregaProjection(viewRepository, commandService, objectMapper);
        endereco = new Endereco("Rua A", "100", "Apt 10", "Centro", "São Paulo", "SP", "01000-000");
    }

    @Test
    @DisplayName("Deve processar evento EntregaAgendada com sucesso")
    void deveProcessarEventoEntregaAgendada() {
        String enderecoCompleto = "Rua A, 100, Apt 10 - Centro, São Paulo/SP - CEP: 01000-000";
        EntregaAgendada evento = new EntregaAgendada(
                "ENT-001", "PED-001", "RES-001", enderecoCompleto, LocalDate.of(2025, 12, 1)
        );

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .pedidoId("PED-001")
                .status("AGENDADA")
                .build();

        when(viewRepository.save(any(EntregaView.class)))
                .thenReturn(Mono.just(view));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).save(any(EntregaView.class));
    }

    @Test
    @DisplayName("Deve processar evento TransporteIniciado com sucesso")
    void deveProcessarEventoTransporteIniciado() {
        TransporteIniciado evento = new TransporteIniciado("ENT-001", "MOT-001", "VEI-001");

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("AGENDADA")
                .build();

        when(viewRepository.findById("ENT-001"))
                .thenReturn(Mono.just(view));
        when(viewRepository.save(any(EntregaView.class)))
                .thenReturn(Mono.just(view));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).findById("ENT-001");
        verify(viewRepository).save(any(EntregaView.class));
    }

    @Test
    @DisplayName("Deve processar evento EntregaConcluida com sucesso")
    void deveProcessarEventoEntregaConcluida() {
        EntregaConcluida evento = new EntregaConcluida(
                "ENT-001", "PED-001", "Cliente", LocalDateTime.of(2025, 12, 1, 14, 30), "Entrega OK"
        );

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("EM_TRANSITO")
                .build();

        when(viewRepository.findById("ENT-001"))
                .thenReturn(Mono.just(view));
        when(viewRepository.save(any(EntregaView.class)))
                .thenReturn(Mono.just(view));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).findById("ENT-001");
        verify(viewRepository).save(any(EntregaView.class));
    }

    @Test
    @DisplayName("Deve processar evento EntregaDevolvida com sucesso")
    void deveProcessarEventoEntregaDevolvida() {
        EntregaDevolvida evento = new EntregaDevolvida(
                "ENT-001", "PED-001", "Cliente ausente", LocalDateTime.of(2025, 12, 1, 14, 30), "MOT-001"
        );

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("EM_TRANSITO")
                .build();

        when(viewRepository.findById("ENT-001"))
                .thenReturn(Mono.just(view));
        when(viewRepository.save(any(EntregaView.class)))
                .thenReturn(Mono.just(view));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).findById("ENT-001");
        verify(viewRepository).save(any(EntregaView.class));
    }

    @Test
    @DisplayName("Deve processar evento EntregaExtraviada com sucesso")
    void deveProcessarEventoEntregaExtraviada() {
        EntregaExtraviada evento = new EntregaExtraviada(
                "ENT-001", "PED-001", "Pacote perdido", LocalDateTime.of(2025, 12, 1, 14, 30), "Centro de Distribuição"
        );

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("EM_TRANSITO")
                .build();

        when(viewRepository.findById("ENT-001"))
                .thenReturn(Mono.just(view));
        when(viewRepository.save(any(EntregaView.class)))
                .thenReturn(Mono.just(view));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento)))
                .verifyComplete();

        verify(viewRepository).findById("ENT-001");
        verify(viewRepository).save(any(EntregaView.class));
    }

    @Test
    @DisplayName("Deve processar ItensSeparados e agendar entrega automaticamente")
    void deveProcessarItensSeparadosEAgendarEntrega() throws Exception {
        String jsonMessage = "{\"id\":\"RES-001\",\"pedidoId\":\"PED-001\",\"enderecoEntrega\":{\"rua\":\"Rua A\",\"numero\":\"100\",\"complemento\":\"Apt 10\",\"bairro\":\"Centro\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"cep\":\"01000-000\"},\"operadorId\":\"OP-001\",\"dataSeparacao\":\"2025-12-01T10:00:00\"}";

        when(commandService.agendarEntrega(anyString(), anyString(), any(), anyString()))
                .thenReturn(Mono.just("ENT-001"));

        Function<Flux<String>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        StepVerifier.create(handler.apply(Flux.just(jsonMessage)))
                .verifyComplete();

        verify(commandService).agendarEntrega(anyString(), anyString(), any(), anyString());
    }

    @Test
    @DisplayName("Deve processar múltiplos eventos em sequência")
    void deveProcessarMultiplosEventosEmSequencia() {
        String enderecoCompleto = "Rua A, 100, Apt 10 - Centro, São Paulo/SP - CEP: 01000-000";
        EntregaAgendada evento1 = new EntregaAgendada(
                "ENT-001", "PED-001", "RES-001", enderecoCompleto, LocalDate.of(2025, 12, 1)
        );
        TransporteIniciado evento2 = new TransporteIniciado("ENT-001", "MOT-001", "VEI-001");
        EntregaConcluida evento3 = new EntregaConcluida(
                "ENT-001", "PED-001", "Cliente", LocalDateTime.of(2025, 12, 1, 14, 30), "OK"
        );

        EntregaView view = EntregaView.builder()
                .id("ENT-001")
                .status("AGENDADA")
                .build();

        when(viewRepository.save(any(EntregaView.class)))
                .thenReturn(Mono.just(view));
        when(viewRepository.findById("ENT-001"))
                .thenReturn(Mono.just(view));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento1, evento2, evento3)))
                .verifyComplete();

        verify(viewRepository, times(3)).save(any(EntregaView.class));
        verify(viewRepository, times(2)).findById("ENT-001");
    }

    @Test
    @DisplayName("Deve ignorar evento desconhecido")
    void deveIgnorarEventoDesconhecido() {
        BaseEvent<?> eventoDesconhecido = new BaseEvent<>("ENT-001") {};

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(eventoDesconhecido)))
                .verifyComplete();

        verifyNoInteractions(viewRepository);
    }

    @Test
    @DisplayName("Deve tratar erro ao salvar view")
    void deveTratarErroAoSalvarView() {
        String enderecoCompleto = "Rua A, 100, Apt 10 - Centro, São Paulo/SP - CEP: 01000-000";
        EntregaAgendada evento = new EntregaAgendada(
                "ENT-001", "PED-001", "RES-001", enderecoCompleto, LocalDate.of(2025, 12, 1)
        );

        when(viewRepository.save(any(EntregaView.class)))
                .thenReturn(Mono.error(new RuntimeException("Erro ao salvar no banco")));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento)))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).save(any(EntregaView.class));
    }

    @Test
    @DisplayName("Deve tratar erro ao buscar view existente")
    void deveTratarErroAoBuscarViewExistente() {
        TransporteIniciado evento = new TransporteIniciado("ENT-001", "MOT-001", "VEI-001");

        when(viewRepository.findById(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao buscar no banco")));

        Function<Flux<BaseEvent<?>>, Mono<Void>> handler = projection.transporteEventsIn();

        StepVerifier.create(handler.apply(Flux.just(evento)))
                .expectError(RuntimeException.class)
                .verify();

        verify(viewRepository).findById("ENT-001");
    }

    @Test
    @DisplayName("Deve ignorar ItensSeparados com endereco nulo")
    void deveIgnorarItensSeparadosComEnderecoNulo() throws Exception {
        String jsonMessage = "{\"id\":\"RES-001\",\"pedidoId\":\"PED-001\",\"enderecoEntrega\":null,\"operadorId\":\"OP-001\",\"dataSeparacao\":\"2025-12-01T10:00:00\"}";

        Function<Flux<String>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        StepVerifier.create(handler.apply(Flux.just(jsonMessage)))
                .verifyComplete();

        verifyNoInteractions(commandService);
    }

    @Test
    @DisplayName("Deve tratar erro ao deserializar evento do Almoxarifado")
    void deveTratarErroAoDeserializarEventoAlmoxarifado() throws Exception {
        String jsonMessage = "{invalid json}";

        Function<Flux<String>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        StepVerifier.create(handler.apply(Flux.just(jsonMessage)))
                .verifyComplete();

        verifyNoInteractions(commandService);
    }

    @Test
    @DisplayName("Deve ignorar mensagem sem campos esperados")
    void deveIgnorarMensagemSemCamposEsperados() throws Exception {
        String jsonMessage = "{\"id\":\"RES-001\"}";

        Function<Flux<String>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        StepVerifier.create(handler.apply(Flux.just(jsonMessage)))
                .verifyComplete();

        verifyNoInteractions(commandService);
    }

    @Test
    @DisplayName("Deve tratar erro ao agendar entrega automaticamente")
    void deveTratarErroAoAgendarEntregaAutomaticamente() throws Exception {
        String jsonMessage = "{\"id\":\"RES-001\",\"pedidoId\":\"PED-001\",\"enderecoEntrega\":{\"rua\":\"Rua A\",\"numero\":\"100\",\"complemento\":\"Apt 10\",\"bairro\":\"Centro\",\"cidade\":\"São Paulo\",\"estado\":\"SP\",\"cep\":\"01000-000\"},\"operadorId\":\"OP-001\",\"dataSeparacao\":\"2025-12-01T10:00:00\"}";

        when(commandService.agendarEntrega(anyString(), anyString(), any(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Erro ao agendar")));

        Function<Flux<String>, Mono<Void>> handler = projection.almoxarifadoEventsIn();

        StepVerifier.create(handler.apply(Flux.just(jsonMessage)))
                .verifyComplete();

        verify(commandService).agendarEntrega(anyString(), anyString(), any(), anyString());
    }
}