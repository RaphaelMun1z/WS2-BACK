package io.github.raphaelmun1z.controllers;

import io.github.raphaelmun1z.controllers.docs.VagaControllerDoc;
import io.github.raphaelmun1z.dto.req.VagaRequestDTO;
import io.github.raphaelmun1z.dto.res.VagaResponseDTO;
import io.github.raphaelmun1z.services.system.VagaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vagas")
public class VagaController implements VagaControllerDoc {
    private final VagaService service;

    public VagaController(VagaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VagaResponseDTO> criarVaga(@RequestBody @Valid VagaRequestDTO dto) {
        VagaResponseDTO response = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/lote")
    public ResponseEntity<Void> criarVagasEmLote(@RequestBody List<VagaRequestDTO> dtos) {
        service.salvarVarias(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<VagaResponseDTO>> buscarTodas(
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<VagaResponseDTO> pagina = service.listarTodas(pageable);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VagaResponseDTO> buscarPorId(@PathVariable String id) {
        VagaResponseDTO response = service.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtro")
    public ResponseEntity<Page<VagaResponseDTO>> buscarComFiltros(
            VagaRequestDTO filtros,
            @PageableDefault(size = 12) Pageable pageable
    ) {
        Page<VagaResponseDTO> resultado = service.listarComFiltros(filtros, pageable);
        return ResponseEntity.ok(resultado);
    }
}