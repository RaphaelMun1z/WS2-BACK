package io.github.raphaelmun1z.jobs;

import io.github.raphaelmun1z.dto.req.VagaRequestDTO;
import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.scrape.*;
import io.github.raphaelmun1z.services.system.VagaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScrapingJob {
    private final VagaService vagaService;

    public ScrapingJob(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @Scheduled(cron = "0 03 22 * * *")
    public void executarBuscaAutomatica() {
        System.out.println("Iniciando scraping...");

        List<Vaga> listaTemporaria = new ArrayList<>();
        listaTemporaria.addAll(new CathoService().buscarVagas());
        listaTemporaria.addAll(new NerdinService().buscarVagas());
        listaTemporaria.addAll(new InfojobsService().buscarVagas());
        listaTemporaria.addAll(new IndeedService().buscarVagas());
        listaTemporaria.addAll(new GlassdoorService().buscarVagas());

        listaTemporaria.forEach(vaga -> {
            try {
                vagaService.salvar(converterParaDto(vaga));
            } catch (Exception e) {
                System.err.println("Erro ao processar vaga: " + vaga.getCodigoVaga());
            }
        });

        System.out.println("Scraping finalizado! Total processado: " + listaTemporaria.size());
    }

    private VagaRequestDTO converterParaDto(Vaga vaga) {
        return new VagaRequestDTO(
                vaga.getCodigoVaga(),
                vaga.getTitulo(),
                vaga.getEmpresa(),
                vaga.getSalario(),
                vaga.getDataAnuncio(),
                vaga.getLocal(),
                vaga.getDescricao(),
                vaga.getRegime(),
                vaga.getLinkCandidatura(),
                vaga.getModalidade(),
                vaga.getFonte()
        );
    }
}
