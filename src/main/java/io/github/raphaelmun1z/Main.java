package io.github.raphaelmun1z;

import io.github.raphaelmun1z.services.*;

public class Main {

    public static void main(String[] args) {
        CathoService cathoScraper = new CathoService();
        NerdinService nerdinScraper = new NerdinService();
        InfojobsService infojobsScraper = new InfojobsService();
        IndeedService indeedScraper = new IndeedService();
        GlassdoorService glassdoorScraper = new GlassdoorService();

        try {
            System.out.println("Iniciando buscas...");
            //cathoScraper.imprimirVagas();
            //nerdinScraper.imprimirVagas();
            //infojobsScraper.imprimirVagas();
            //indeedScraper.imprimirVagas();
            glassdoorScraper.imprimirVagas();
        } finally {
            System.out.println("Fechando navegadores...");
            cathoScraper.fechar();
            nerdinScraper.fechar();
            infojobsScraper.fechar();
            indeedScraper.fechar();
            glassdoorScraper.fechar();
        }
    }
}