package com.petfriends.transporte.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Value Object Endereco")
class EnderecoTest {

    @Test
    @DisplayName("Deve criar endereço válido com todos os campos")
    void deveCriarEnderecoValidoComTodosCampos() {
        Endereco endereco = new Endereco(
                "Rua das Flores",
                "123",
                "Apto 45",
                "Centro",
                "São Paulo",
                "SP",
                "01310-100"
        );

        assertEquals("Rua das Flores", endereco.getRua());
        assertEquals("123", endereco.getNumero());
        assertEquals("Apto 45", endereco.getComplemento());
        assertEquals("Centro", endereco.getBairro());
        assertEquals("São Paulo", endereco.getCidade());
        assertEquals("SP", endereco.getEstado());
        assertEquals("01310-100", endereco.getCep());
    }

    @Test
    @DisplayName("Deve criar endereço válido sem complemento")
    void deveCriarEnderecoValidoSemComplemento() {
        Endereco endereco = new Endereco(
                "Av Paulista",
                "1000",
                null,
                "Bela Vista",
                "São Paulo",
                "SP",
                "01310-100"
        );

        assertNull(endereco.getComplemento());
        assertEquals("Av Paulista", endereco.getRua());
    }

    @Test
    @DisplayName("Deve aceitar CEP com hífen")
    void deveAceitarCepComHifen() {
        Endereco endereco = new Endereco(
                "Rua A",
                "1",
                null,
                "Centro",
                "São Paulo",
                "SP",
                "01310-100"
        );

        assertEquals("01310-100", endereco.getCep());
    }

    @Test
    @DisplayName("Deve aceitar CEP sem hífen")
    void deveAceitarCepSemHifen() {
        Endereco endereco = new Endereco(
                "Rua A",
                "1",
                null,
                "Centro",
                "São Paulo",
                "SP",
                "01310100"
        );

        assertEquals("01310100", endereco.getCep());
    }

    @Test
    @DisplayName("Deve lançar exceção quando rua é nula")
    void deveLancarExcecaoQuandoRuaNula() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco(null, "123", null, "Centro", "São Paulo", "SP", "01310-100")
        );

        assertEquals("Rua é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando rua é vazia")
    void deveLancarExcecaoQuandoRuaVazia() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("", "123", null, "Centro", "São Paulo", "SP", "01310-100")
        );

        assertEquals("Rua é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando rua contém apenas espaços")
    void deveLancarExcecaoQuandoRuaApenasEspacos() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("   ", "123", null, "Centro", "São Paulo", "SP", "01310-100")
        );

        assertEquals("Rua é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando número é nulo")
    void deveLancarExcecaoQuandoNumeroNulo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", null, null, "Centro", "São Paulo", "SP", "01310-100")
        );

        assertEquals("Número é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando número é vazio")
    void deveLancarExcecaoQuandoNumeroVazio() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "", null, "Centro", "São Paulo", "SP", "01310-100")
        );

        assertEquals("Número é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando bairro é nulo")
    void deveLancarExcecaoQuandoBairroNulo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, null, "São Paulo", "SP", "01310-100")
        );

        assertEquals("Bairro é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando bairro é vazio")
    void deveLancarExcecaoQuandoBairroVazio() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "", "São Paulo", "SP", "01310-100")
        );

        assertEquals("Bairro é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cidade é nula")
    void deveLancarExcecaoQuandoCidadeNula() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "Centro", null, "SP", "01310-100")
        );

        assertEquals("Cidade é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cidade é vazia")
    void deveLancarExcecaoQuandoCidadeVazia() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "Centro", "", "SP", "01310-100")
        );

        assertEquals("Cidade é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estado é nulo")
    void deveLancarExcecaoQuandoEstadoNulo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "Centro", "São Paulo", null, "01310-100")
        );

        assertEquals("Estado é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estado é vazio")
    void deveLancarExcecaoQuandoEstadoVazio() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "Centro", "São Paulo", "", "01310-100")
        );

        assertEquals("Estado é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CEP é nulo")
    void deveLancarExcecaoQuandoCepNulo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "Centro", "São Paulo", "SP", null)
        );

        assertEquals("CEP inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CEP tem formato inválido")
    void deveLancarExcecaoQuandoCepFormatoInvalido() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "Centro", "São Paulo", "SP", "123")
        );

        assertEquals("CEP inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CEP tem letras")
    void deveLancarExcecaoQuandoCepTemLetras() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Endereco("Rua A", "123", null, "Centro", "São Paulo", "SP", "0131a-100")
        );

        assertEquals("CEP inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar endereço com complemento")
    void deveFormatarEnderecoComComplemento() {
        Endereco endereco = new Endereco(
                "Rua das Flores",
                "123",
                "Apto 45",
                "Centro",
                "São Paulo",
                "SP",
                "01310-100"
        );

        String formatado = endereco.formatado();

        assertEquals("Rua das Flores, 123 Apto 45 - Centro, São Paulo/SP - CEP: 01310-100", formatado);
    }

    @Test
    @DisplayName("Deve formatar endereço sem complemento")
    void deveFormatarEnderecoSemComplemento() {
        Endereco endereco = new Endereco(
                "Av Paulista",
                "1000",
                null,
                "Bela Vista",
                "São Paulo",
                "SP",
                "01310-100"
        );

        String formatado = endereco.formatado();

        assertEquals("Av Paulista, 1000 - Bela Vista, São Paulo/SP - CEP: 01310-100", formatado);
    }

    @Test
    @DisplayName("Deve formatar endereço com complemento vazio")
    void deveFormatarEnderecoComComplementoVazio() {
        Endereco endereco = new Endereco(
                "Rua A",
                "100",
                "",
                "Centro",
                "São Paulo",
                "SP",
                "01310-100"
        );

        String formatado = endereco.formatado();

        assertEquals("Rua A, 100 - Centro, São Paulo/SP - CEP: 01310-100", formatado);
    }

    @Test
    @DisplayName("toString deve retornar endereço formatado")
    void toStringDeveRetornarEnderecoFormatado() {
        Endereco endereco = new Endereco(
                "Rua B",
                "200",
                "Sala 10",
                "Jardim",
                "Campinas",
                "SP",
                "13010-200"
        );

        assertEquals(endereco.formatado(), endereco.toString());
    }

    @Test
    @DisplayName("Dois endereços iguais devem ser equals")
    void doisEnderecosIguaisDevemSerEquals() {
        Endereco endereco1 = new Endereco(
                "Rua A", "100", "Apto 1", "Centro", "São Paulo", "SP", "01310-100"
        );
        Endereco endereco2 = new Endereco(
                "Rua A", "100", "Apto 1", "Centro", "São Paulo", "SP", "01310-100"
        );

        assertEquals(endereco1, endereco2);
        assertEquals(endereco1.hashCode(), endereco2.hashCode());
    }

    @Test
    @DisplayName("Dois endereços diferentes não devem ser equals")
    void doisEnderecosDiferentesNaoDevemSerEquals() {
        Endereco endereco1 = new Endereco(
                "Rua A", "100", null, "Centro", "São Paulo", "SP", "01310-100"
        );
        Endereco endereco2 = new Endereco(
                "Rua B", "200", null, "Centro", "São Paulo", "SP", "01310-100"
        );

        assertNotEquals(endereco1, endereco2);
    }
}

