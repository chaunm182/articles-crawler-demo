package com.example.crawlerdemo.crawler.zingnews;

import com.example.crawlerdemo.util.ArticleAttributes;
import com.example.crawlerdemo.util.StringsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class ZingNewsCrawler {

    private final ChromeOptions chromeOptions;

    public Set<String> getAllUrlCategories() {
        log.info("Getting zing news categories...");
        WebDriver webDriver = new ChromeDriver(chromeOptions);

        webDriver.get(ArticleAttributes.NewsPage.ZINGNEWS.home);

        WebElement menuButton = webDriver.findElement(By.cssSelector("#zing-header > div.page-wrapper > nav > ul > li.more"));
        menuButton.click();

        List<WebElement> categories = webDriver.findElements(By.cssSelector("#zing-header > div.category-popup.active > div > nav > ul li.parent > a"));

        Set<String> categoriesUrl = categories.stream().parallel()
                .map(webElement -> webElement.getAttribute("href"))
                .filter(text -> !text.equals(StringsUtil.EMPTY) && !text.contains("/du-lich.html"))
                .collect(Collectors.toSet());

        webDriver.close();
        return categoriesUrl;

    }
}
