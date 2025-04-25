import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class TimeStoreTest {
    private WebDriver driver;

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

    @Test
    public void hunt() {
        List<String> urls = List.of(
                "https://www.timestore.pl/wyszukiwanie?q=SRPD",
                "https://www.timestore.pl/zegarki-meskie-orient?sort=cheapest",
                "https://www.timestore.pl/seiko-prospex?sort=cheapest",
                "https://www.timestore.pl/citizen-promaster?sort=cheapest"
        );

        scrapeDataFromUrls(getExecutionTimeStamp(), urls);
    }

    private void scrapeDataFromUrls(String timestamp, List<String> urls) {
        for (var url : urls) {
            scrapeDataFromUrl(timestamp, url);
        }
    }

    private void scrapeDataFromUrl(String timestamp, String url) {
        driver.get(url);
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        webDriverWait.until(d -> driver.findElements(By.cssSelector(".w-pb-list__item")).size() > 1);
        List<WebElement> elements = driver.findElements(By.cssSelector(".w-pb-list__item"));

        for (var element : elements) {
            List<WebElement> title = element.findElements(By.xpath(".//h3/a"));
            List<WebElement> price = element.findElements(By.xpath(".//p/span"));

            if (!title.isEmpty() && !price.isEmpty()) {
                System.out.println(title.get(0).getText() + " | " + price.get(0).getText());
                logPrice(timestamp, title.get(0).getText(), price.get(0).getText());
            }
        }
    }

    private String getExecutionTimeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private void logPrice(String timestamp, String name, String price) {
        try (FileWriter writer = new FileWriter("data.csv", true)) {
            writer.append(String.join(",", timestamp, name, parsePrice(price)));
            writer.append("\n");

        } catch (IOException e) {
            throw new IllegalStateException("There was problem while creating csv file!", e);
        }
    }

    private String parsePrice(String price) {
        return price
                .replace("zł", "")
                .replace("\u00A0", "")
                .replace(" ", "")
                .replace(",", ".")
                .trim();
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
