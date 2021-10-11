package com.example.crawlerdemo.crawler.dantri;

import com.example.crawlerdemo.crawler.BaseCategoryCrawler;
import com.example.crawlerdemo.repository.ArticleRepository;
import okhttp3.OkHttpClient;
import org.jsoup.nodes.Document;

public class DanTriCategoryCrawler extends BaseCategoryCrawler {


    protected DanTriCategoryCrawler(String baseUrl, OkHttpClient httpClient, ArticleRepository articleRepository) {
        super(baseUrl, httpClient, articleRepository, DanTriArticleCrawler.class);
    }

    @Override
    protected boolean hasNextPage(int currentPage, Document document) {
        return currentPage <= 30;
    }

    @Override
    protected String assignArticlesSelector() {
        return "ul.dt-list--lg > li > div.news-item";
    }
}
