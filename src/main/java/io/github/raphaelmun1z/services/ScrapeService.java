package io.github.raphaelmun1z.services;

import io.github.raphaelmun1z.entities.Vaga;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ScrapeService {
    private WebDriver driver;
    private WebDriverWait wait;

    public ScrapeService() {
        this.iniciarDriver();
    }

    private void iniciarDriver() {
        FirefoxOptions options = new FirefoxOptions();

        options.addArguments("-private");
        this.driver = new FirefoxDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<Vaga> buscarVagasCatho() {
        System.out.println("BUSCANDO - CATHO...");
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

                    // --- LOCAL ---
                    try {
                        WebElement localEl = card.findElement(By.cssSelector("button a[title]"));
                        novaVaga.setLocal(localEl.getAttribute("title"));
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

    private void destacarElemento(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            js.executeScript("arguments[0].style.border='4px solid red'; arguments[0].style.backgroundColor='#fffacd';", element);
            Thread.sleep(1000);
        } catch (Exception e) {}
    }

    public void fechar() {
        if (driver != null) {
            driver.quit();
        }
    }
}