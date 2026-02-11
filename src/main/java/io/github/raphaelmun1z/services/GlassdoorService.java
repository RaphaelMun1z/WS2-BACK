package io.github.raphaelmun1z.services;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.interfaces.ScrapeInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class GlassdoorService extends ScrapeService implements ScrapeInterface {
    public List<Vaga> buscarVagas() {
        List<Vaga> vagas = new ArrayList<>();
        try {
            String url = "https://www.glassdoor.com.br/Vaga/trabalho-remoto-brasil-ti-vagas-SRCH_IL.0,22_IS12226_KO23,25.htm";
            driver.get(url);

            String seletorLista = "ul[class*='JobsList_jobsList']";
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletorLista)));

            List<WebElement> elementosVagas = driver.findElements(By.cssSelector(seletorLista + " > li[data-test='jobListing']"));

            for (WebElement card : elementosVagas) {
                destacarElemento(card);

                Vaga novaVaga = new Vaga();
                novaVaga.setFonte("Glassdoor");
                boolean dadosValidos = false;

                try {
                    // --- TÍTULO E LINK ---
                    WebElement linkTitulo = card.findElement(By.cssSelector("a[data-test='job-title']"));
                    String textoTitulo = linkTitulo.getText();
                    String linkVaga = linkTitulo.getAttribute("href");

                    if (!textoTitulo.isEmpty()) {
                        novaVaga.setTitulo(textoTitulo);
                        novaVaga.setLinkCandidatura(linkVaga);
                        dadosValidos = true;
                    }

                    // --- CÓDIGO DA VAGA ---
                    try {
                        String codigoHtml = card.getAttribute("data-jobid");

                        if (codigoHtml != null && !codigoHtml.isEmpty()) {
                            novaVaga.setCodigoVaga(codigoHtml);
                        } else {
                            String[] partesUrl = linkVaga.split("jl=");
                            if (partesUrl.length > 1) {
                                String codigoExtraido = partesUrl[1].split("&")[0]; // Pega o numero antes do proximo &
                                novaVaga.setCodigoVaga(codigoExtraido);
                            }
                        }
                    } catch (Exception e) {
                        novaVaga.setCodigoVaga("N/A");
                    }

                    // --- EMPRESA ---
                    try {
                        WebElement empresaEl = card.findElement(By.cssSelector("span[class*='EmployerProfile_compactEmployerName']"));
                        String nomeEmpresa = empresaEl.getText();
                        novaVaga.setEmpresa(nomeEmpresa);
                    } catch (Exception e) {
                        novaVaga.setEmpresa("N/A");
                    }

                    // --- SALÁRIO ---
                    try {
                        WebElement salarioEl = card.findElement(By.cssSelector("[data-test='detailSalary']"));
                        novaVaga.setSalario(salarioEl.getText().replaceAll("\\(.*?\\)", "").trim()); // Remove texto extra entre parenteses
                    } catch (Exception e) {
                        novaVaga.setSalario("N/A");
                    }

                    // --- LOCAL e MODALIDADE ---
                    try {
                        WebElement localEl = card.findElement(By.cssSelector("[data-test='emp-location']"));
                        String local = localEl.getText();
                        novaVaga.setLocal(local);

                        String localLower = local.toLowerCase();
                        if (localLower.contains("remoto") || localLower.contains("remote")) {
                            novaVaga.setModalidade("Home Office");
                        } else if (localLower.contains("híbrido") || localLower.contains("hybrid")) {
                            novaVaga.setModalidade("Híbrido");
                        } else {
                            novaVaga.setModalidade("Presencial");
                        }
                    } catch (Exception e) {
                        novaVaga.setLocal("N/A");
                        novaVaga.setModalidade("N/A");
                    }

                    // --- REGIME ---
                    try {
                        WebElement descEl = card.findElement(By.cssSelector("[data-test='descSnippet']"));
                        String descText = descEl.getText().toLowerCase();

                        if (descText.contains("clt") || descText.contains("efetivo")) {
                            novaVaga.setRegime("CLT");
                        } else if (descText.contains("pj") || descText.contains("jurídica")) {
                            novaVaga.setRegime("PJ");
                        } else if (descText.contains("estágio") || descText.contains("intern")) {
                            novaVaga.setRegime("Estágio");
                        } else {
                            novaVaga.setRegime("N/A");
                        }
                    } catch (Exception e) {
                        novaVaga.setRegime("N/A");
                    }

                    // --- DESCRIÇÃO ---
                    try {
                        WebElement descEl = card.findElement(By.cssSelector("[data-test='descSnippet']"));
                        novaVaga.setDescricao(descEl.getText());
                    } catch (Exception e) {
                        novaVaga.setDescricao("N/A");
                    }

                    if (dadosValidos) {
                        vagas.add(novaVaga);
                    }
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar vagas: " + e.getMessage());
        }
        return vagas;
    }

    public void imprimirVagas(){
        System.out.println("BUSCANDO EM: GLASSDOOR");
        this.iniciarDriver();
        List<Vaga> vagas = this.buscarVagas();
        System.out.println("Encontrei " + vagas.size() + " vagas:");
        vagas.forEach(System.out::println);
    }
}
