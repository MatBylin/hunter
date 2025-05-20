import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public abstract class Base {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disk-cache-size=1");
        chromeOptions.addArguments("--media-cache-size=1");
        chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--aggressive-cache-discard");
        driver = new ChromeDriver(chromeOptions);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    abstract void scrapeDataFromUrl(String timestamp, String url);

    protected void scrapeUrls(String timestamp, List<String> urls) {
        for (var url : urls) {
            scrapeDataFromUrl(timestamp, url);
        }
    }

    protected String getExecutionTimeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    protected void logPrice(String sourceFile, String timestamp, String name, String price) {
        try (FileWriter writer = new FileWriter(sourceFile, true)) {
            writer.append(String.join(",", timestamp, name, parsePrice(price)));
            writer.append("\n");

        } catch (IOException e) {
            throw new IllegalStateException("There was problem while creating csv file!", e);
        }
    }

    protected String parsePrice(String price) {
        return price
                .replace("zł", "")
                .replace("\u00A0", "")
                .replace(" ", "")
                .replace(",", ".")
                .trim();
    }
}
