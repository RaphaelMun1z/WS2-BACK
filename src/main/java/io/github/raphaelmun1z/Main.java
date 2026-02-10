package io.github.raphaelmun1z;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.ScrapeService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ScrapeService scraper = new ScrapeService();

        try {
            System.out.println("Iniciando busca...");
            List<Vaga> vagasCatho = scraper.buscarVagasCatho();
            System.out.println("Encontrei " + vagasCatho.size() + " vagas:");
            vagasCatho.forEach(System.out::println);
        } finally {
            scraper.fechar();
        }
    }
}