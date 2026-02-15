package io.github.raphaelmun1z.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.raphaelmun1z.dto.req.VagaRequestDTO;
import io.github.raphaelmun1z.dto.res.VagaResponseDTO;
import io.github.raphaelmun1z.services.system.VagaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VagaController.class)
@DisplayName("Testes do VagaController")
class VagaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VagaService service;

    private VagaRequestDTO criarVagaRequest() {
        return new VagaRequestDTO(
                "123", "Dev Java", "TechCorp", "10k", "01/01/2026",
                "Remote", "Desc...", "CLT", "http://link.com", "Híbrido", "LinkedIn"
        );
    }

    private VagaResponseDTO criarVagaResponse(String id) {
        return new VagaResponseDTO(
                id,
                "123",
                "Dev Java",
                "TechCorp",
                "10k",
                "01/01/2026",
                "Remote",
                "Desc...",
                "CLT",
                "http://link.com",
                "Híbrido",
                "LinkedIn"
        );
    }

    @Test
    @DisplayName("POST /vagas - Deve criar uma vaga e retornar 201 Created")
    void deveCriarVaga() throws Exception {
        // Given
        VagaRequestDTO requestDTO = criarVagaRequest();
        VagaResponseDTO responseDTO = criarVagaResponse("uuid-1");

        when(service.salvar(any(VagaRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/vagas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("uuid-1"))
                .andExpect(jsonPath("$.titulo").value("Dev Java"))
                .andExpect(jsonPath("$.fonte").value("LinkedIn"));

        verify(service, times(1)).salvar(any(VagaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /vagas/lote - Deve processar lista e retornar 201 Created")
    void deveCriarVagasEmLote() throws Exception {
        // Given
        List<VagaRequestDTO> listaDTO = List.of(criarVagaRequest(), criarVagaRequest());

        doNothing().when(service).salvarVarias(anyList());

        // When & Then
        mockMvc.perform(post("/vagas/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listaDTO)))
                .andExpect(status().isCreated());

        verify(service, times(1)).salvarVarias(anyList());
    }

    @Test
    @DisplayName("GET /vagas - Deve retornar página de vagas e status 200 OK")
    void deveBuscarTodas() throws Exception {
        // Given
        List<VagaResponseDTO> content = List.of(criarVagaResponse("uuid-1"));
        Page<VagaResponseDTO> page = new PageImpl<>(content);

        when(service.listarTodas(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/vagas")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("uuid-1"))
                .andExpect(jsonPath("$.content[0].titulo").value("Dev Java"));

        verify(service).listarTodas(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /vagas/{id} - Deve retornar vaga específica quando ID existe")
    void deveBuscarPorId() throws Exception {
        // Given
        String id = "uuid-1";
        VagaResponseDTO responseDTO = criarVagaResponse(id);

        when(service.buscarPorId(id)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/vagas/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.empresa").value("TechCorp"));

        verify(service).buscarPorId(id);
    }

    @Test
    @DisplayName("GET /vagas/filtro - Deve filtrar usando Query Params")
    void deveBuscarComFiltros() throws Exception {
        // Given
        List<VagaResponseDTO> content = List.of(criarVagaResponse("uuid-1"));
        Page<VagaResponseDTO> page = new PageImpl<>(content);

        when(service.listarComFiltros(any(VagaRequestDTO.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/vagas/filtro")
                        .param("fonte", "LinkedIn")
                        .param("titulo", "Java")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].fonte").value("LinkedIn"));

        verify(service).listarComFiltros(any(VagaRequestDTO.class), any(Pageable.class));
    }
}