package io.github.raphaelmun1z.services.scrape.interfaces;

import io.github.raphaelmun1z.entities.Vaga;

import java.util.List;

public interface ScrapeInterface {
    List<Vaga> buscarVagas();
    void imprimirVagas();
}
