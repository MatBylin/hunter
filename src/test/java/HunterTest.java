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

import java.time.Duration;
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
        driver.get("https://www.timestore.pl/wyszukiwanie?q=seiko+5");

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        webDriverWait.until(d -> driver.findElements(By.cssSelector(".w-pb-list__item")).size() > 25);

        List<WebElement> elements = driver.findElements(By.cssSelector(".w-pb-list__item"));
        System.out.printf("Found %s items!%n", elements.size());
        for (var element : elements) {
            List<WebElement> title = element.findElements(By.xpath(".//h3/a"));
            List<WebElement> price = element.findElements(By.xpath(".//p/span"));

            if (!title.isEmpty() && !price.isEmpty()) {
                System.out.println(title.get(0).getText() + " | " + price.get(0).getText());
            }
        }
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
