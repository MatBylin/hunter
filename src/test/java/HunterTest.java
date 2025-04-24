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
import java.util.List;

@Slf4j
public class HunterTest {
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
        driver.get("https://www.timestore.pl/wyszukiwanie?q=SRPD");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        webDriverWait.until(d -> driver.findElements(By.cssSelector(".w-pb-list__item")).size() > 3);

        List<WebElement> elements = driver.findElements(By.cssSelector(".w-pb-list__item"));
        System.out.printf("Found %s items!%n", elements.size());
        String timestamp = LocalDateTime.now().toString();
        for (var element : elements) {
            List<WebElement> title = element.findElements(By.xpath(".//h3/a"));
            List<WebElement> price = element.findElements(By.xpath(".//p/span"));

            if (!title.isEmpty() && !price.isEmpty()) {
                System.out.println(title.get(0).getText() + " | " + price.get(0).getText());
                logPrice(timestamp, title.get(0).getText(), price.get(0).getText());
            }
        }
    }

    private void logPrice(String timestamp, String name, String price) {
        try (FileWriter writer = new FileWriter("data/data.csv", true)) {
            writer.append(String.join(",", timestamp, name, parsePrice(price)));
            writer.append("\n");

        } catch (IOException e) {
            throw new IllegalStateException("There was problem while creating csv file!", e);
        }
    }

    private String parsePrice(String price) {
        return price
                .replace("zł", "")      // remove currency
                .replace("\u00A0", "")  // remove non-breaking space (used in some price strings)
                .replace(" ", "")       // remove regular spaces (thousands separator)
                .replace(",", ".")      // replace comma with dot
                .trim();                // remove any extra whitespace
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
