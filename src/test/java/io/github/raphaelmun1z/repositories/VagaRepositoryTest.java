package io.github.raphaelmun1z.repositories;

import io.github.raphaelmun1z.entities.Vaga;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Testes do Repositório de Vagas")
class VagaRepositoryTest {
    @Autowired
    private VagaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve retornar vaga com sucesso quando buscar por fonte e código existentes")
    void deveEncontrarVagaPorFonteECodigo() {
        // Given
        Vaga vaga = criarVaga("12345", "LinkedIn");
        entityManager.persist(vaga);

        // When
        Optional<Vaga> result = repository.findByFonteAndCodigoVaga("LinkedIn", "12345");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getTitulo()).isEqualTo("Desenvolvedor Java");
    }

    @Test
    @DisplayName("Deve retornar vazio quando buscar por fonte e código inexistentes")
    void naoDeveEncontrarVagaInexistente() {
        // Given
        // Nenhuma vaga persistida com esses dados

        // When
        Optional<Vaga> result = repository.findByFonteAndCodigoVaga("SiteDesconhecido", "99999");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve diferenciar vagas com mesmo código mas fontes diferentes")
    void deveDiferenciarPorFonte() {
        // Given
        Vaga vagaLinkedin = criarVaga("100", "LinkedIn");
        Vaga vagaIndeed = criarVaga("100", "Indeed");

        entityManager.persist(vagaLinkedin);
        entityManager.persist(vagaIndeed);

        // When
        Optional<Vaga> resultLinkedin = repository.findByFonteAndCodigoVaga("LinkedIn", "100");
        Optional<Vaga> resultIndeed = repository.findByFonteAndCodigoVaga("Indeed", "100");

        // Then
        assertThat(resultLinkedin).isPresent();
        assertThat(resultLinkedin.get().getFonte()).isEqualTo("LinkedIn");

        assertThat(resultIndeed).isPresent();
        assertThat(resultIndeed.get().getFonte()).isEqualTo("Indeed");
    }

    private Vaga criarVaga(String codigo, String fonte) {
        Vaga vaga = new Vaga();
        vaga.setCodigoVaga(codigo);
        vaga.setFonte(fonte);
        vaga.setTitulo("Desenvolvedor Java");
        vaga.setEmpresa("Empresa X");
        vaga.setLinkCandidatura("http://link.com");
        return vaga;
    }
}