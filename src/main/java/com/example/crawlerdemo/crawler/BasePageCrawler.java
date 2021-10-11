package com.example.crawlerdemo.crawler;

import com.example.crawlerdemo.entity.Article;
import com.example.crawlerdemo.exception.CrawlerException;
import com.example.crawlerdemo.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class BasePageCrawler {
    private final Elements articleElements;

    private final OkHttpClient client;

    private final ArticleRepository articleRepository;

    private final Class<? extends BaseArticleCrawler> clazz;

    private final LocalDateTime crawlTime;

    public int run() {
        try {
            long startTime = System.currentTimeMillis();
            //crawl per article
            ExecutorService threadPool = Executors.newFixedThreadPool(articleElements.size());

            Class[] cArgs = new Class[]{OkHttpClient.class, Element.class, LocalDateTime.class};
            Constructor<? extends BaseArticleCrawler> baseArticleConstructor = clazz.getDeclaredConstructor(cArgs);
            baseArticleConstructor.setAccessible(true);

            List<CompletableFuture<Article>> articleFutureList = articleElements
                    .stream()
                    .map(element -> {
                        try {
                            return CompletableFuture.supplyAsync(baseArticleConstructor.newInstance(client, element, crawlTime), threadPool)
                                    .exceptionally(throwable -> {
                                        log.error(null, throwable);
                                        return null;
                                    });
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            log.error("Error", e);
                        }
                        return null;
                    })
                    .collect(Collectors.toList());
            baseArticleConstructor.setAccessible(false);

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(articleFutureList.toArray(new CompletableFuture[articleElements.size()]));

            CompletableFuture<List<Article>> allArticlesFuture = allFutures
                    .thenApply(v -> articleFutureList.stream()
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
            threadPool.shutdown();

            //insert after getting all articles in current page
            List<Article> articles = allArticlesFuture.get();
            if(!articles.isEmpty()) {
                articleRepository.saveAll(articles);
            }
            log.info("Bulk {}/{} articles successful after {}ms",articles.size(), articleElements.size(), System.currentTimeMillis() - startTime);

            return articles.size();
        } catch (ExecutionException ex) {
            throw new CrawlerException("Error when crawling page", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new CrawlerException("Error when crawling page", ex);
        } catch (NoSuchMethodException e) {
            throw new CrawlerException("Reflection error", e);
        }
    }
}
