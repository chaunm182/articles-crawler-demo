package com.example.crawlerdemo.crawler.dantri;

import com.example.crawlerdemo.repository.ArticleRepository;
import com.example.crawlerdemo.util.ArticleAttributes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j
public class DanTriScheduleCrawler {
    private final ArticleRepository articleRepository;
    private final OkHttpClient client;
    private final DanTriCrawler danTriCrawler;
    private final TaskExecutor taskExecutor;

    @Scheduled(fixedDelay = 30, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    @Async("taskExecutor")
    public void scheduleCrawlTask() throws IOException {
        log.info("Starting crawl today data from {}", ArticleAttributes.NewsPage.DANTRI);
        Set<String> categoriesUrl = danTriCrawler.getAllUrlCategories();
        for (String categoryUrl : categoriesUrl) {
            String baseCateUrl = ArticleAttributes.NewsPage.DANTRI.home.concat(categoryUrl).concat("/trang-{0}.htm");
            taskExecutor.execute(new DanTriCategoryCrawler(baseCateUrl, client, articleRepository));
        }
    }
}
