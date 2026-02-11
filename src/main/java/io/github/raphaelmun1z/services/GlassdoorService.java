package io.github.raphaelmun1z.services;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.interfaces.ScrapeInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlassdoorService extends ScrapeService implements ScrapeInterface {
    public List<Vaga> buscarVagas() {
        this.iniciarDriver();
        List<Vaga> vagas = new ArrayList<>();
        try {
            String url = "https://www.glassdoor.com.br/Vaga/trabalho-remoto-brasil-ti-vagas-SRCH_IL.0,22_IS12226_KO23,25.htm";
            driver.get(url);

            String seletorLista = "ul[class*='JobsList_jobsList']";
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletorLista)));

            List<WebElement> elementosVagas = driver.findElements(By.cssSelector(seletorLista + " > li[data-test='jobListing']"));

            for (WebElement card : elementosVagas) {
                try {
                    destacarElemento(card);

                    Vaga novaVaga = new Vaga();
                    novaVaga.setFonte("Glassdoor");
                    boolean dadosValidos = false;

                    // --- TÍTULO E LINK ---
                    try {
                        WebElement linkTitulo = card.findElement(By.cssSelector("a[data-test='job-title']"));
                        String textoTitulo = linkTitulo.getText();
                        String linkVaga = linkTitulo.getAttribute("href");

                        if (!textoTitulo.isEmpty()) {
                            novaVaga.setTitulo(textoTitulo);
                            novaVaga.setLinkCandidatura(linkVaga);
                            dadosValidos = true;
                        }
                    } catch (Exception e) {}

                    // --- CÓDIGO DA VAGA ---
                    try {
                        String idVaga = card.getAttribute("data-jobid");
                        if (idVaga != null) {
                            novaVaga.setCodigoVaga(idVaga);
                        } else {
                            novaVaga.setCodigoVaga("N/A");
                        }
                    } catch (Exception e) {
                        novaVaga.setCodigoVaga("N/A");
                    }

                    // --- EMPRESA ---
                    try {
                        WebElement empresaEl = card.findElement(By.cssSelector("span[class*='EmployerProfile_compactEmployerName']"));
                        novaVaga.setEmpresa(empresaEl.getText());
                    } catch (Exception e) {
                        novaVaga.setEmpresa("N/A");
                    }

                    // --- SALÁRIO ---
                    try {
                        WebElement salarioEl = card.findElement(By.cssSelector("[data-test='detailSalary']"));
                        novaVaga.setSalario(salarioEl.getText().replaceAll("\\(.*?\\)", "").trim());
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

                    // --- REGIME E DESCRIÇÃO ---
                    try {
                        WebElement descEl = card.findElement(By.cssSelector("[data-test='descSnippet']"));
                        String descText = descEl.getText();
                        String descLower = descText.toLowerCase();

                        if (descLower.contains("clt") || descLower.contains("efetivo")) {
                            novaVaga.setRegime("CLT");
                        } else if (descLower.contains("pj") || descLower.contains("jurídica")) {
                            novaVaga.setRegime("PJ");
                        } else if (descLower.contains("estágio") || descLower.contains("intern")) {
                            novaVaga.setRegime("Estágio");
                        } else {
                            novaVaga.setRegime("N/A");
                        }
                        novaVaga.setDescricao(descText);
                    } catch (Exception e) {
                        novaVaga.setRegime("N/A");
                        novaVaga.setDescricao("N/A");
                    }

                    if (dadosValidos) {
                        vagas.add(novaVaga);
                    }
                } catch (Exception e) {}
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar vagas: " + e.getMessage());
        } finally {
            this.fechar();
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
