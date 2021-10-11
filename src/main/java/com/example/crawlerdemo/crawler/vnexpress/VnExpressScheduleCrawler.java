package com.example.crawlerdemo.crawler.vnexpress;

import com.example.crawlerdemo.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class VnExpressScheduleCrawler {
    private final ArticleRepository articleRepository;
    private final OkHttpClient client;
    private final VnExpressCrawler crawler;
    private final TaskExecutor taskExecutor;

    @Scheduled(fixedDelay = 35, initialDelay = 3, timeUnit = TimeUnit.MINUTES)
    public void scheduleCrawlTask() throws IOException {
        Map<String, String> category = crawler.getAllCategories();

        Set<String> categoriesId = category.keySet();
        long yesterday = LocalDate.now(Clock.systemDefaultZone())
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
                .getEpochSecond();
        String crawlTime = String.valueOf(yesterday);
        for (String id : categoriesId) {
            taskExecutor.execute(new VnExpressCategoryCrawler(client, taskExecutor, id, crawlTime, crawlTime, articleRepository));
        }

    }

}
