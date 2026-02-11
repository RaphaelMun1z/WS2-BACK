package io.github.raphaelmun1z.services;

import io.github.raphaelmun1z.entities.Vaga;
import io.github.raphaelmun1z.services.interfaces.ScrapeInterface;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class NerdinService extends ScrapeService implements ScrapeInterface {

    public List<Vaga> buscarVagas() {
        this.iniciarDriver();
        List<Vaga> vagas = new ArrayList<>();
        try {
            String url = "https://www.nerdin.com.br/vagas.php?CodigoNivel=4,3,7,6&filtro_home_office=1";
            driver.get(url);

            String seletorLista = "div#vagas-container";
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(seletorLista)));

            List<WebElement> elementosVagas = driver.findElements(By.cssSelector(seletorLista + " > div.vaga-card"));

            for (WebElement card : elementosVagas) {
                destacarElemento(card);

                Vaga novaVaga = new Vaga();
                novaVaga.setFonte("Nerdin");
                boolean dadosValidos = false;

                try {
                    // --- TÍTULO E LINK ---
                    WebElement linkTitulo = card.findElement(By.cssSelector("h3.vaga-titulo"));
                    String textoTitulo = linkTitulo.getText();

                    WebElement botaoAcessarVaga = card.findElement(By.cssSelector("a.btn-sm"));
                    String linkVaga = botaoAcessarVaga.getAttribute("href");

                    if (!textoTitulo.isEmpty()) {
                        novaVaga.setTitulo(textoTitulo);
                        novaVaga.setLinkCandidatura(linkVaga);
                        dadosValidos = true;
                    }

                    // --- CÓDIGO DA VAGA ---
                    try {
                        String[] partesUrl = linkVaga.split("/");
                        String codigoExtraido = partesUrl[partesUrl.length - 1];
                        String codigoLimpo = codigoExtraido.replaceAll("\\D", "");

                        if (codigoLimpo.matches("\\d+")) {
                            novaVaga.setCodigoVaga(codigoLimpo);
                        }
                    } catch (Exception e) {
                        novaVaga.setCodigoVaga("N/A");
                    }

                    // --- EMPRESA ---
                    try {
                        WebElement empresaEl = card.findElement(By.cssSelector(".vaga-empresa"));
                        String nomeEmpresa = empresaEl.getText();
                        novaVaga.setEmpresa(nomeEmpresa);
                    } catch (Exception e) {
                        novaVaga.setEmpresa("N/A");
                    }

                    // --- SALÁRIO ---
                    try {
                        WebElement salarioEl = card.findElement(By.cssSelector("div.vaga-salario"));
                        novaVaga.setSalario(salarioEl.getText());
                    } catch (Exception e) {
                        novaVaga.setSalario("N/A");
                    }

                    // --- LOCAL e MODALIDADE ---
                    try {
                        String local = card.findElement(By.cssSelector("div.vaga-local")).getText();
                        novaVaga.setLocal(local);

                        String localLower = local.toLowerCase();
                        if (localLower.contains("home office")) {
                            novaVaga.setModalidade("Home Office");
                        } else {
                            novaVaga.setModalidade("N/A");
                        }
                    } catch (Exception e) {
                        novaVaga.setLocal("N/A");
                    }

                    // --- REGIME ---
                    try {
                        boolean ehPJ = !card.findElements(By.cssSelector("i.fa-briefcase")).isEmpty();
                        boolean ehCLT = !card.findElements(By.cssSelector("i.fa-file-contract")).isEmpty();

                        if (ehPJ && ehCLT) {
                            novaVaga.setRegime("CLT e PJ");
                        } else if (ehPJ) {
                            novaVaga.setRegime("PJ");
                        } else if (ehCLT) {
                            novaVaga.setRegime("CLT");
                        } else {
                            novaVaga.setRegime("N/A");
                        }
                    } catch (Exception e) {
                        novaVaga.setRegime("N/A");
                    }

                    // --- DESCRIÇÃO ---
                    novaVaga.setDescricao("N/A");

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
        System.out.println("BUSCANDO EM: NERDIN");
        this.iniciarDriver();
        List<Vaga> vagas = this.buscarVagas();
        System.out.println("Encontrei " + vagas.size() + " vagas:");
        vagas.forEach(System.out::println);
    }
}
