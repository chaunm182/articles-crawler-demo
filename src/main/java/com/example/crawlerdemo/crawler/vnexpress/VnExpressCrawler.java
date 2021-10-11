package com.example.crawlerdemo.crawler.vnexpress;

import com.example.crawlerdemo.exception.CrawlerException;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class VnExpressCrawler {
    private final OkHttpClient client;
    private static final Set<String> EXCLUDES = Collections.singleton("1003834"); //exclude video page

    public Map<String, String> getAllCategories() throws IOException {
        Request request = new Request.Builder()
                .url(ArticleAttributes.NewsPage.VNEXPRESS.home)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        if (Objects.isNull(responseBody)) throw new CrawlerException("Error when parse html body");
        Document document = Jsoup.parse(responseBody.string());

        Elements categoryElements = document.select("nav.main-nav > ul.parent > li[data-id]");

        Map<String, String> result = new HashMap<>();
        for (Element category : categoryElements) {
            String dataId = category.attr("data-id").trim();
            if (!EXCLUDES.contains(dataId)) {
                Element categoryUrl = category.selectFirst("a");
                if (Objects.nonNull(categoryUrl)) {
                    result.put(dataId, categoryUrl.attr("href"));
                }
            }
        }
        return result;
    }
}
