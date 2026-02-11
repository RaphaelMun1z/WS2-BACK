package io.github.raphaelmun1z.services;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.interfaces.ScrapeInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class InfojobsService extends ScrapeService implements ScrapeInterface {

    public List<Vaga> buscarVagas() {
        this.iniciarDriver();
        List<Vaga> vagas = new ArrayList<>();
        try {
            String url = "https://www.infojobs.com.br/vagas-de-emprego-estagio+ti-em-sao-paulo,-sp-trabalho-home-office.aspx";
            driver.get(url);

            String seletorContainer = "div.js_vacanciesGridFragment";
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletorContainer)));

            List<WebElement> elementosVagas = driver.findElements(By.cssSelector("div.card"));

            for (WebElement card : elementosVagas) {
                destacarElemento(card);

                Vaga novaVaga = new Vaga();
                novaVaga.setFonte("Infojobs");
                boolean dadosValidos = false;

                try {
                    // --- TÍTULO E LINK ---
                    WebElement tituloEl = card.findElement(By.cssSelector("h2"));
                    String textoTitulo = tituloEl.getText();

                    WebElement linkEl = card.findElement(By.xpath(".//h2/parent::a"));
                    String linkVaga = linkEl.getAttribute("href");

                    if (!textoTitulo.isEmpty()) {
                        novaVaga.setTitulo(textoTitulo);
                        novaVaga.setLinkCandidatura(linkVaga);
                        dadosValidos = true;
                    }

                    // --- CÓDIGO DA VAGA ---
                    try {
                        String codigo = linkVaga.replaceAll("\\D+", "");
                        novaVaga.setCodigoVaga(codigo);
                    } catch (Exception e) {
                        novaVaga.setCodigoVaga("N/A");
                    }

                    // --- EMPRESA ---
                    try {
                        WebElement empresaEl = card.findElement(By.cssSelector("div.d-flex.align-items-baseline a"));
                        String nomeEmpresa = empresaEl.getText().replace("verificada", "").trim();
                        novaVaga.setEmpresa(nomeEmpresa);
                    } catch (Exception e) {
                        novaVaga.setEmpresa("N/A");
                    }

                    // --- SALÁRIO ---
                    try {
                        WebElement salarioEl = card.findElement(By.xpath(".//*[local-name()='svg' and contains(@class, 'icon-money')]/parent::div"));

                        String salarioTexto = salarioEl.getAttribute("textContent").trim();

                        if (salarioTexto.isEmpty()) {
                            novaVaga.setSalario("A combinar");
                        } else {
                            novaVaga.setSalario(salarioTexto);
                        }
                    } catch (Exception e) {
                        novaVaga.setSalario("N/A");
                    }

                    // --- LOCAL E MODALIDADE ---
                    try {
                        WebElement localEl = card.findElement(By.cssSelector("div.mb-8"));
                        String textoLocal = localEl.getText();
                        novaVaga.setLocal(textoLocal);

                        boolean temIconeHO = !card.findElements(By.cssSelector("svg.icon-user-home")).isEmpty();
                        String localLower = textoLocal.toLowerCase();

                        if (temIconeHO || localLower.contains("home office")) {
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

                    // --- DESCRIÇÃO ---
                    try {
                        WebElement descEl = card.findElement(By.xpath(".//div[contains(@class, 'd-inline-flex')]/following-sibling::div[contains(@class, 'text-medium')]"));

                        String textoDesc = descEl.getText().trim();

                        if (textoDesc.length() > 250) {
                            textoDesc = textoDesc.substring(0, 250) + "...";
                        }

                        novaVaga.setDescricao(textoDesc);
                    } catch (Exception e) {
                        novaVaga.setDescricao("N/A");
                    }

                    // --- REGIME ---
                    novaVaga.setRegime("N/A");

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

    public void imprimirVagas() {
        System.out.println("BUSCANDO EM: INFOJOBS");
        this.iniciarDriver();
        List<Vaga> vagas = this.buscarVagas();
        System.out.println("Encontrei " + vagas.size() + " vagas:");
        vagas.forEach(System.out::println);
    }
}
