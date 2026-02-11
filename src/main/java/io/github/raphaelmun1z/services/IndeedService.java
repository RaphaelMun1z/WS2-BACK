package io.github.raphaelmun1z.services;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.interfaces.ScrapeInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IndeedService extends ScrapeService implements ScrapeInterface {
    public List<Vaga> buscarVagas() {
        System.out.println("BUSCANDO EM: INDEED");
        List<Vaga> vagas = new ArrayList<>();

        try {
            String url = "https://br.indeed.com/jobs?q=ti&l=&sc=0kf%3Aattr%28DSQF7%29attr%28VDTG7%29%3B&from=searchOnDesktopSerp&vjk=bb03cf567c5605df";
            driver.get(url);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mosaic-provider-jobcards")));

            List<WebElement> cardElements = driver.findElements(By.cssSelector("div.cardOutline"));

            for (WebElement card : cardElements) {
                try {
                    destacarElemento(card);

                    Vaga novaVaga = new Vaga();
                    novaVaga.setFonte("Indeed");
                    boolean dadosValidos = false;

                    // --- TÍTULO E LINK ---
                    try {
                        WebElement linkEl = card.findElement(By.cssSelector("h2.jobTitle a"));
                        String titulo = linkEl.findElement(By.cssSelector("span")).getAttribute("title");
                        String link = linkEl.getAttribute("href");

                        if (titulo != null && !titulo.isEmpty()) {
                            novaVaga.setTitulo(titulo);
                            novaVaga.setLinkCandidatura(link);

                            String jk = card.getAttribute("data-jk");
                            novaVaga.setCodigoVaga(jk != null ? jk : "N/A");

                            dadosValidos = true;
                        }
                    } catch (Exception e) {
                        continue;
                    }

                    // --- EMPRESA ---
                    try {
                        WebElement empresaEl = card.findElement(By.cssSelector("span[data-testid='company-name']"));
                        novaVaga.setEmpresa(empresaEl.getText());
                    } catch (Exception e) {
                        novaVaga.setEmpresa("Confidencial");
                    }

                    // --- LOCAL ---
                    try {
                        WebElement localEl = card.findElement(By.cssSelector("div[data-testid='text-location']"));
                        novaVaga.setLocal(localEl.getText());

                        String localLower = novaVaga.getLocal().toLowerCase();
                        if (localLower.contains("remoto") || localLower.contains("home office")) {
                            novaVaga.setModalidade("Home Office");
                        } else if (localLower.contains("híbrido")) {
                            novaVaga.setModalidade("Híbrido");
                        } else {
                            novaVaga.setModalidade("Presencial");
                        }
                    } catch (Exception e) {
                        novaVaga.setLocal("N/A");
                        novaVaga.setModalidade("N/A");
                    }

                    // --- SALÁRIO e REGIME ---
                    try {
                        List<WebElement> metaItems = card.findElements(By.cssSelector("div.metadataContainer div[data-testid='attribute_snippet_testid']"));

                        String salario = "N/A";
                        String regime = "N/A";

                        for (WebElement item : metaItems) {
                            String texto = item.getText();
                            if (texto.contains("R$") || texto.contains("mês") || texto.contains("ano")) {
                                salario = texto;
                            } else if (texto.toLowerCase().contains("estágio") || texto.toLowerCase().contains("clt") || texto.toLowerCase().contains("efetivo") || texto.toLowerCase().contains("tempo")) {
                                regime = texto;
                            }
                        }
                        novaVaga.setSalario(salario);
                        novaVaga.setRegime(regime);
                    } catch (Exception e) {
                        novaVaga.setSalario("N/A");
                        novaVaga.setRegime("N/A");
                    }

                    // --- DESCRIÇÃO ---
                    try {
                        WebElement snippetEl = card.findElement(By.cssSelector("div[data-testid='belowJobSnippet']")); // Ou .job-snippet
                        if(snippetEl.getText().isEmpty()){
                            novaVaga.setDescricao("N/A");
                        }else {
                            novaVaga.setDescricao(snippetEl.getText());
                        }
                    } catch (Exception e) {
                        novaVaga.setDescricao("N/A");
                    }

                    if (dadosValidos) {
                        vagas.add(novaVaga);
                    }

                } catch (Exception e) {
                    System.out.println("Erro ao ler card: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Erro geral Indeed: " + e.getMessage());
        }
        return vagas;
    }

    public void imprimirVagas() {
        System.out.println("BUSCANDO EM: INDEED");
        this.iniciarDriver();
        List<Vaga> vagas = this.buscarVagas();
        System.out.println("Encontrei " + vagas.size() + " vagas:");
        vagas.forEach(System.out::println);
    }
}