package io.github.raphaelmun1z.services;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.interfaces.ScrapeInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CathoService extends ScrapeService implements ScrapeInterface {

    public List<Vaga> buscarVagas() {
        List<Vaga> vagas = new ArrayList<>();
        try {
            String url = "https://www.catho.com.br/vagas/jr/?order=score&area_id%5B0%5D=51&work_model%5B0%5D=remote";
            driver.get(url);

            String seletorLista = "ul.search-result-custom_jobList__lVIvI";
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletorLista)));

            List<WebElement> elementosVagas = driver.findElements(By.cssSelector(seletorLista + " > li"));

            for (WebElement card : elementosVagas) {
                destacarElemento(card);

                Vaga novaVaga = new Vaga();
                novaVaga.setFonte("Catho");
                boolean dadosValidos = false;

                try {
                    // --- TÍTULO E LINK ---
                    WebElement linkTitulo = card.findElement(By.cssSelector("h2 a"));

                    String textoTitulo = linkTitulo.getText();
                    String linkVaga = linkTitulo.getAttribute("href");

                    if (!textoTitulo.isEmpty()) {
                        novaVaga.setTitulo(textoTitulo);
                        novaVaga.setLinkCandidatura(linkVaga);
                        dadosValidos = true;
                    }

                    // --- CÓDIGO DA VAGA ---
                    try {
                        String[] partesUrl = linkVaga.split("/");
                        String codigoExtraido = partesUrl[partesUrl.length - 1];
                        if (codigoExtraido.matches("\\d+")) {
                            novaVaga.setCodigoVaga(codigoExtraido);
                        }
                    } catch (Exception e) {
                        novaVaga.setCodigoVaga("N/A");
                    }

                    // --- EMPRESA ---
                    try {
                        WebElement empresaEl = card.findElement(By.xpath(".//h2/ancestor::div[1]/following-sibling::p"));
                        String textoBruto = empresaEl.getText();
                        String empresaLimpa = textoBruto.replace("Por que?", "")
                                .replace("Por que", "") // Prevenção extra
                                .trim();
                        novaVaga.setEmpresa(empresaLimpa);
                    } catch (Exception e) {
                        novaVaga.setEmpresa("N/A");
                    }

                    // --- SALÁRIO ---
                    try {
                        WebElement salarioEl = card.findElement(By.cssSelector("div[class*='salaryText']"));
                        novaVaga.setSalario(salarioEl.getText());
                    } catch (Exception e) {
                        novaVaga.setSalario("N/A");
                    }

                    // --- LOCAL e MODALIDADE ---
                    try {
                        WebElement localEl = card.findElement(By.cssSelector("button a[title]"));
                        novaVaga.setLocal(localEl.getAttribute("title"));
                        if(Objects.equals(novaVaga.getLocal(), "Home Office/HO")){
                            novaVaga.setModalidade("Home Office");
                        } else {
                            novaVaga.setModalidade("N/A");
                        }
                    } catch (Exception e) {
                        novaVaga.setLocal("N/A");
                    }

                    // --- DESCRIÇÃO ---
                    try {
                        WebElement descEl = card.findElement(By.cssSelector(".job-description"));
                        String descricao = descEl.getAttribute("textContent");

                        if (descricao.length() > 200) {
                            descricao = descricao.substring(0, 200) + "...";
                        }
                        novaVaga.setDescricao(descricao);
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
        System.out.println("BUSCANDO EM: CATHO");
        this.iniciarDriver();
        List<Vaga> vagas = this.buscarVagas();
        System.out.println("Encontrei " + vagas.size() + " vagas:");
        vagas.forEach(System.out::println);
    }
}
