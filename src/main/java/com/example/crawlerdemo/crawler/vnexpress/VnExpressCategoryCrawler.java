package com.example.crawlerdemo.crawler.vnexpress;

import com.example.crawlerdemo.crawler.BasePageCrawler;
import com.example.crawlerdemo.exception.CrawlerException;
import com.example.crawlerdemo.repository.ArticleRepository;
import com.example.crawlerdemo.util.ArticleAttributes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
public class VnExpressCategoryCrawler implements Runnable {
    private final OkHttpClient client;

    private final TaskExecutor taskExecutor;

    private final String vnexpressCategoryId;

    private final String fromDate;

    private final String toDate;

    private final ArticleRepository articleRepository;

    private static final String CATEGORY_SEARCH_URL = "/category/day?cateid={0}&fromdate={1}&todate={2}&page={3}";

    @Override
    public void run() {
        Document document;
        try {
            int page = 1;
            do {
                //make http call
                String url = MessageFormat.format(ArticleAttributes.NewsPage.VNEXPRESS.home.concat(CATEGORY_SEARCH_URL),
                        vnexpressCategoryId,
                        fromDate,
                        toDate,
                        page);
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                if (response.code() != HttpStatus.OK.value()) {
                    throw new CrawlerException("Response status: " + response.code());
                }
                if (Objects.isNull(responseBody)) throw new CrawlerException("Error when parse html body");

                //parse html get elements
                document = Jsoup.parse(responseBody.byteStream(), StandardCharsets.UTF_8.name(), ArticleAttributes.NewsPage.VNEXPRESS.home);
                Elements articleElements = document.select("div.list-news-subfolder > article.item-news");
                log.info("Server response successful. Starting crawl articles in page {}. Numbers of element: {}", url, articleElements.size());
                if (!articleElements.isEmpty()) {
//                    taskExecutor.execute(new BasePageCrawler(articleElements, client, articleRepository,VnExpressArticleCrawler.class));
                }

                //sleep 500ms per page
                page++;
                TimeUnit.MILLISECONDS.sleep(500);
            } while (hasNextPage(document));
        } catch (IOException e) {
            throw new CrawlerException("Connection error", e);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new CrawlerException("Thread interrupted", ex);
        }


    }

    private boolean hasNextPage(Document document) {
        Element nextPageElement = document.selectFirst("div#pagination a.next-page");
        return !Objects.isNull(nextPageElement) && !nextPageElement.hasClass("disable");

    }
}
