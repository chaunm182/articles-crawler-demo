package com.example.crawlerdemo.crawler;

import com.example.crawlerdemo.exception.CrawlerException;
import com.example.crawlerdemo.repository.ArticleRepository;
import com.example.crawlerdemo.util.ArticleAttributes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public abstract class BaseCategoryCrawler implements Runnable {
    private final String baseUrl;

    private final String articlesSelector;

    private final OkHttpClient httpClient;

    private final ArticleRepository articleRepository;

    private final Class<? extends BaseArticleCrawler> clazz;

    protected abstract boolean hasNextPage(int currentPage, Document document);

    protected abstract String assignArticlesSelector();

    protected BaseCategoryCrawler(String baseUrl, OkHttpClient httpClient, ArticleRepository articleRepository, Class<? extends BaseArticleCrawler> clazz) {
        this.articlesSelector = assignArticlesSelector();
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.articleRepository = articleRepository;
        this.clazz = clazz;
    }

    @Override
    public void run() {
        try {
            int currentPage = 1;
            int resultPerPage;
            int totalElementsPerPage;
            Document document;
            LocalDateTime crawlTime = getCrawlTime();
            do {
                String url = MessageFormat.format(baseUrl, String.valueOf(currentPage));
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = httpClient.newCall(request).execute();
                ResponseBody body = response.body();
                if (Objects.isNull(body)) throw new CrawlerException("Error when parse body");
                document = Jsoup.parse(body.string());
                Elements articleElements = document.select(articlesSelector);
                if (!articleElements.isEmpty()) {
                    totalElementsPerPage = articleElements.size();
                    log.info("Start crawl articles in url {}", url);
                    resultPerPage = new BasePageCrawler(articleElements, httpClient, articleRepository, clazz, crawlTime).run();
                } else break;
                currentPage++;

            } while (resultPerPage == totalElementsPerPage && hasNextPage(currentPage, document));
        } catch (IOException ex) {
            throw new CrawlerException("ERROR", ex);
        }

    }

    private LocalDateTime getCrawlTime() {
        //TODO get max time in db
        return ArticleAttributes.INIT_PUBLISH_TIME;
    }
}
