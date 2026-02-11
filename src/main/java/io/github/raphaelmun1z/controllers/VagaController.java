package io.github.raphaelmun1z.controllers;

import io.github.raphaelmun1z.dto.req.VagaRequestDTO;
import io.github.raphaelmun1z.dto.res.VagaResponseDTO;
import io.github.raphaelmun1z.services.system.VagaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vagas")
public class VagaController {
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
    public ResponseEntity<List<VagaResponseDTO>> buscarTodas() {
        List<VagaResponseDTO> lista = service.listarTodas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VagaResponseDTO> buscarPorId(@PathVariable String id) {
        VagaResponseDTO response = service.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<VagaResponseDTO>> buscarComFiltros(VagaRequestDTO filtros) {
        List<VagaResponseDTO> resultado = service.listarComFiltros(filtros);
        return ResponseEntity.ok(resultado);
    }
}