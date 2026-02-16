package io.github.raphaelmun1z.integrationstests.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.raphaelmun1z.config.TestConfig;
import io.github.raphaelmun1z.dto.req.VagaRequestDTO;
import io.github.raphaelmun1z.dto.res.VagaResponseDTO;
import io.github.raphaelmun1z.integrationstests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class VagaControllerIntegrationTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static VagaRequestDTO vagaRequest;
    private static String vagaId;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        vagaRequest = new VagaRequestDTO(
                "COD123",
                "Desenvolvedor Java Senior",
                "TechCorp",
                "R$ 15.000",
                "01/02/2026",
                "São Paulo",
                "Vaga para especialista Spring Boot",
                "CLT",
                "http://techcorp.com/vagas/123",
                "Remoto",
                "LinkedIn"
        );
    }

    @BeforeEach
    public void setupRequest() {
        specification = new RequestSpecBuilder()
                .setBasePath("/api/vagas")
                .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Integração: Deve criar uma vaga e retornar 201 Created")
    void deveCriarVaga() throws JsonProcessingException {
        var content = given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .body(vagaRequest)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        VagaResponseDTO vagaCriada = objectMapper.readValue(content, VagaResponseDTO.class);

        vagaId = vagaCriada.id();

        assertNotNull(vagaCriada.id());
        assertEquals("Desenvolvedor Java Senior", vagaCriada.titulo());
        assertEquals("LinkedIn", vagaCriada.fonte());
        assertEquals("TechCorp", vagaCriada.empresa());
    }

    @Test
    @Order(2)
    @DisplayName("Integração: Deve retornar uma vaga por ID")
    void deveBuscarPorId() throws JsonProcessingException {
        assertNotNull(vagaId, "O ID da vaga não deve ser nulo. O teste de criação falhou?");

        var content = given().spec(specification)
                .pathParam("id", vagaId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        VagaResponseDTO vagaEncontrada = objectMapper.readValue(content, VagaResponseDTO.class);

        assertEquals(vagaId, vagaEncontrada.id());
        assertEquals("Desenvolvedor Java Senior", vagaEncontrada.titulo());
    }

    @Test
    @Order(3)
    @DisplayName("Integração: Deve retornar página de vagas")
    void deveListarVagasPaginadas() {
        given().spec(specification)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .queryParam("sort", "titulo,asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("content", hasSize(greaterThan(0)))
                .body("content[0].titulo", notNullValue())
                .body("page.totalElements", greaterThan(0));
    }

    @Test
    @Order(4)
    @DisplayName("Integração: Deve filtrar vagas")
    void deveFiltrarVagas() {
        given().spec(specification)
                .queryParam("fonte", "LinkedIn")
                .queryParam("empresa", "TechCorp")
                .when()
                .get("/filtro")
                .then()
                .statusCode(200)
                .body("content", hasSize(greaterThan(0)))
                .body("content[0].fonte", equalTo("LinkedIn"))
                .body("content[0].empresa", equalTo("TechCorp"));
    }

    @Test
    @Order(5)
    @DisplayName("Integração: Deve criar vagas em lote")
    void deveCriarVagasEmLote() {
        VagaRequestDTO v1 = new VagaRequestDTO("C1", "Dev Jr", "Emp A", "3k", "01/01", "SP", "Desc", "PJ", "link", "Hibrido", "Indeed");
        VagaRequestDTO v2 = new VagaRequestDTO("C2", "Dev Pl", "Emp B", "6k", "01/01", "RJ", "Desc", "CLT", "link", "Remoto", "Glassdoor");

        List<VagaRequestDTO> lote = List.of(v1, v2);

        given().spec(specification)
                .contentType(TestConfig.CONTENT_TYPE_JSON)
                .body(lote)
                .when()
                .post("/lote")
                .then()
                .statusCode(201);
    }
}
