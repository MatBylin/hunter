import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

@Slf4j
public class TimeStoreTest extends Base {
    @Test
    public void hunt() {
        List<String> urls = List.of(
                "https://www.timestore.pl/wyszukiwanie?q=SRPD",
                "https://www.timestore.pl/zegarki-meskie-orient?sort=cheapest",
                "https://www.timestore.pl/seiko-prospex?sort=cheapest",
                "https://www.timestore.pl/citizen-promaster?sort=cheapest"
        );

        scrapeUrls(getExecutionTimeStamp(), urls);
    }

    @Override
    void scrapeDataFromUrl(String timestamp, String url) {
        driver.get(url);
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        webDriverWait.until(d -> driver.findElements(By.cssSelector(".w-pb-list__item")).size() > 1);
        List<WebElement> elements = driver.findElements(By.cssSelector(".w-pb-list__item"));

        for (var element : elements) {
            List<WebElement> title = element.findElements(By.xpath(".//h3/a"));
            List<WebElement> price = element.findElements(By.xpath(".//p/span"));

            if (!title.isEmpty() && !price.isEmpty()) {
                logPrice("data.csv", timestamp, title.get(0).getText(), price.get(0).getText());
            }
        }
    }
}
