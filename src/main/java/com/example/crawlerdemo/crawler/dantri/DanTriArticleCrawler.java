package com.example.crawlerdemo.crawler.dantri;

import com.example.crawlerdemo.crawler.BaseArticleCrawler;
import com.example.crawlerdemo.util.ArticleAttributes;
import com.example.crawlerdemo.util.StringsUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
public class DanTriArticleCrawler extends BaseArticleCrawler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d/MM/yyyy - HH:mm");

    public DanTriArticleCrawler(OkHttpClient httpClient, Element articleElement, LocalDateTime crawlTime) {
        super(httpClient, articleElement, crawlTime);
    }

    @Override
    protected String assignImageSelectorValue() {
        return "a > div.dt-thumbnail > img";
    }

    @Override
    protected String assignTitleSelectorValue() {
        return "h3.news-item__title a";
    }

    @Override
    protected String assignDescriptionSelectorValue() {
        return "article > div.clearfix > div > div.dt-news__sapo > h2";
    }

    @Override
    protected String assignBodySelectorValue() {
        return "article > div.clearfix > div > div.dt-news__content";
    }

    @Override
    protected String assignPublishDateSelectorValue() {
        return "div.dt-news__header > div.dt-news__meta > span.dt-news__time";
    }

    @Override
    protected ArticleAttributes.NewsPage assignPage() {
        return ArticleAttributes.NewsPage.DANTRI;
    }

    @Override
    protected String assignRegexId() {
        return "^.+-(\\d+).html?$";
    }

    @Override
    protected LocalDateTime getPublishDate(Document articleDocument) {
        try{
            Element publishDateElement  = articleDocument.selectFirst(this.getPublishDateSelector());
            if(Objects.isNull(publishDateElement)) return null;
            String fullDateStr = publishDateElement.text();
            String publishDate = fullDateStr.substring(fullDateStr.indexOf(StringsUtil.COMMAS)+1).trim();
            return LocalDateTime.parse(publishDate, FORMATTER);
        }catch (Exception ex){
            log.error("Parse date error", ex);
        }
        return null;
    }
}
