package com.example.crawlerdemo.crawler.dantri;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@AllArgsConstructor
public class DanTriCrawler {
    private final OkHttpClient okHttpClient;

    private static final Set<String> CATEGORIES_EXCLUDES = new HashSet<>(Arrays.asList("/video-page", "/emagazine", "/photo-story", "/infographic", "/su-kien"));


    public Set<String> getAllUrlCategories() throws IOException {
        Request request = new Request.Builder()
                .url(ArticleAttributes.NewsPage.DANTRI.home)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        ResponseBody body = response.body();
        if (Objects.isNull(body)) throw new CrawlerException("Connection Error");

        Document document = Jsoup.parse(body.string());
        Elements categoryElements = document.select("ol.site-menu__list > li.dropdown a[data-utm]");

        Set<String> result = new HashSet<>();

        Pattern pattern = Pattern.compile("^(/.[^/]*)(/.*)?\\.htm$");
        for (Element e : categoryElements) {
            Matcher matcher = pattern.matcher(e.attr("href"));
            if (matcher.find()) {
                String href = matcher.group(1);
                if (!CATEGORIES_EXCLUDES.contains(href)) result.add(href);
            }
        }
        return result;
    }

}
