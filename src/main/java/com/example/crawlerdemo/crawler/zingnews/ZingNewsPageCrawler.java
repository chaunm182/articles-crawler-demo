package com.example.crawlerdemo.crawler.zingnews;

import com.example.crawlerdemo.crawler.vnexpress.VnExpressArticleCrawler;
import com.example.crawlerdemo.entity.Article;
import com.example.crawlerdemo.exception.CrawlerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class ZingNewsPageCrawler implements Runnable {
    private final Elements articleElements;

    private final RestHighLevelClient restHighLevelClient;

    private final ObjectMapper objectMapper;

    private final OkHttpClient client;

    private LocalDate crawlDate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy").withLocale(Locale.getDefault());

    @Override
    public void run() {
//
//        List<Element> filteredByDateElements = articleElements.stream().filter(this::filterByCrawlDate).collect(Collectors.toList());
//        if(filteredByDateElements.isEmpty()) return;
//
//        ExecutorService threadPool = Executors.newFixedThreadPool(filteredByDateElements.size());
//        List<CompletableFuture<Article>> articleFutureList = filteredByDateElements
//                .stream()
//                .map(element -> CompletableFuture.supplyAsync(new ZingNewsArticleCrawler(client, element), threadPool)
//                        .exceptionally(throwable -> {
//                            log.error(null, throwable);
//                            return null;
//                        }))
//                .collect(Collectors.toList());
//
//        CompletableFuture<Void> allFutures = CompletableFuture.allOf(articleFutureList.toArray(new CompletableFuture[articleElements.size()]));
//
//        CompletableFuture<List<Article>> allArticlesFuture = allFutures
//                .thenApply(v -> articleFutureList.stream().filter(Objects::nonNull).map(CompletableFuture::join).collect(Collectors.toList()));
//
//        threadPool.shutdown();

    }

    private boolean filterByCrawlDate(Element articleElement) {
        Element publishDateElement = articleElement.selectFirst("header > p.article-meta > span > span.date");
        if (Objects.isNull(publishDateElement)) throw new CrawlerException("Publish date error");
        LocalDate publishDate = LocalDate.parse(publishDateElement.text(), FORMATTER);
        return publishDate.isEqual(crawlDate);


    }
}
