package io.github.raphaelmun1z;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        CathoService cathoScraper = new CathoService();
        NerdinService nerdinScraper = new NerdinService();
        InfojobsService infojobsScraper = new InfojobsService();
        IndeedService indeedScraper = new IndeedService();
        GlassdoorService glassdoorScraper = new GlassdoorService();

        try {
            System.out.println("Iniciando buscas...");
            Set<Vaga> todasAsVagas = new HashSet<>();
            todasAsVagas.addAll(cathoScraper.buscarVagas());
            todasAsVagas.addAll(nerdinScraper.buscarVagas());
            todasAsVagas.addAll(infojobsScraper.buscarVagas());
            todasAsVagas.addAll(indeedScraper.buscarVagas());
            todasAsVagas.addAll(glassdoorScraper.buscarVagas());
            todasAsVagas.forEach(System.out::println);
            System.out.println("Total: " + todasAsVagas.size());
        } finally {
            System.out.println("Buscas finalizadas...");
        }
    }
}