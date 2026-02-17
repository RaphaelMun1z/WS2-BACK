package io.github.raphaelmun1z.services.scrape;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ScrapeService {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public ScrapeService() {}

    protected void iniciarDriver() {
        if (this.driver == null) {
            FirefoxOptions options = new FirefoxOptions();

            options.addArguments("--headless");

            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            options.addPreference("permissions.default.image", 2);
            options.addPreference("dom.ipc.plugins.enabled.libflashplayer.so", "false");

            options.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0");
            options.addPreference("dom.webdriver.enabled", false);

            this.driver = new FirefoxDriver(options);

            this.driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));

            this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        }
    }

    protected void destacarElemento(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        } catch (Exception e) {}
    }

    public void fechar() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Erro ao fechar driver: " + e.getMessage());
            }
            driver = null;
        }
    }
}