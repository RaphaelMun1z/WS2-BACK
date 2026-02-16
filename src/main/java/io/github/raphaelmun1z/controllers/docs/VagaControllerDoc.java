package io.github.raphaelmun1z.controllers.docs;

import io.github.raphaelmun1z.dto.req.VagaRequestDTO;
import io.github.raphaelmun1z.dto.res.VagaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Vagas", description = "Gerenciamento de vagas de emprego")
public interface VagaControllerDoc {
    @Operation(summary = "Criar nova vaga", description = "Cadastra uma nova vaga no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    ResponseEntity<VagaResponseDTO> criarVaga(@RequestBody VagaRequestDTO dto);

    @Operation(summary = "Criar vagas em lote", description = "Cadastra uma lista de vagas de uma única vez.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vagas criadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na validação da lista")
    })
    ResponseEntity<Void> criarVagasEmLote(@RequestBody List<VagaRequestDTO> dtos);

    @Operation(summary = "Listar todas as vagas", description = "Retorna uma lista paginada de todas as vagas.")
    @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso")
    ResponseEntity<Page<VagaResponseDTO>> buscarTodas(@ParameterObject Pageable pageable);

    @Operation(summary = "Buscar vaga por ID", description = "Retorna os detalhes de uma vaga específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vaga encontrada"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    })
    ResponseEntity<VagaResponseDTO> buscarPorId(@Parameter(description = "ID da vaga", required = true) @PathVariable String id);

    @Operation(summary = "Filtrar vagas", description = "Busca vagas com base em critérios como título, empresa, local, etc.")
    @ApiResponse(responseCode = "200", description = "Filtro realizado com sucesso")
    ResponseEntity<Page<VagaResponseDTO>> buscarComFiltros(
            @ParameterObject VagaRequestDTO filtros,
            @ParameterObject Pageable pageable
    );
}
