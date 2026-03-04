package io.github.raphaelmun1z.jobs;

import io.github.raphaelmun1z.dto.req.VagaRequestDTO;
import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.scrape.*;
import io.github.raphaelmun1z.services.system.VagaService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class ScrapingJob {
    private final VagaService vagaService;

    public ScrapingJob(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @PostConstruct
    public void executarAoIniciar() {
        System.out.println(">>> SCRAPING JOB INICIALIZADO <<<");
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                executarBuscaAutomatica();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.setName("Thread-Scraping-Manual");
        thread.start();
    }

    @Scheduled(cron = "0 0 */6 * * *")
    public void executarBuscaAutomatica() {
        System.out.println("=== [START] Iniciando Ciclo de Scraping ===");
        List<Vaga> listaTemporaria = new ArrayList<>();

        executarScrapingPorFonte("Catho", new CathoService(), listaTemporaria);
        executarScrapingPorFonte("Nerdin", new NerdinService(), listaTemporaria);
        executarScrapingPorFonte("Infojobs", new InfojobsService(), listaTemporaria);
        executarScrapingPorFonte("Indeed", new IndeedService(), listaTemporaria);
        executarScrapingPorFonte("Glassdoor", new GlassdoorService(), listaTemporaria);

        System.out.println("=== [PROCESS] Total de vagas capturadas: " + listaTemporaria.size() + " ===");

        if (listaTemporaria.isEmpty()) {
            System.out.println("=== [WARN] Nenhuma vaga encontrada em nenhuma fonte. Verifique bloqueios de IP. ===");
            return;
        }

        int salvosComSucesso = 0;
        for (Vaga vaga : listaTemporaria) {
            try {
                vagaService.salvar(converterParaDto(vaga));
                salvosComSucesso++;
            } catch (Exception e) {
                System.err.println("Erro ao salvar vaga [" + vaga.getTitulo() + "] da fonte " + vaga.getFonte() + ": " + e.getMessage());
            }
        }

        System.out.println("=== [END] Scraping finalizado! Salvos no banco: " + salvosComSucesso + "/" + listaTemporaria.size() + " ===");
    }

    private void executarScrapingPorFonte(String nomeFonte, Object service, List<Vaga> listaGeral) {
        try {
            System.out.println("-> Buscando na " + nomeFonte + "...");

            List<Vaga> vagasDaFonte = new ArrayList<>();

            if (service instanceof CathoService s) vagasDaFonte = s.buscarVagas();
            else if (service instanceof NerdinService s) vagasDaFonte = s.buscarVagas();
            else if (service instanceof InfojobsService s) vagasDaFonte = s.buscarVagas();
            else if (service instanceof IndeedService s) vagasDaFonte = s.buscarVagas();
            else if (service instanceof GlassdoorService s) vagasDaFonte = s.buscarVagas();

            if (vagasDaFonte != null && !vagasDaFonte.isEmpty()) {
                listaGeral.addAll(vagasDaFonte);
                System.out.println("   [OK] " + nomeFonte + " retornou " + vagasDaFonte.size() + " vagas.");
            } else {
                System.out.println("   [EMPTY] " + nomeFonte + " não retornou vagas.");
            }
        } catch (Exception e) {
            System.err.println("   [ERROR] Falha crítica ao buscar na " + nomeFonte + ": " + e.getMessage());
        }
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
