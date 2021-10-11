package com.example.crawlerdemo.crawler.zingnews;

import com.example.crawlerdemo.crawler.BaseArticleCrawler;
import com.example.crawlerdemo.util.ArticleAttributes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ZingNewsArticleCrawler extends BaseArticleCrawler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");


    protected ZingNewsArticleCrawler(OkHttpClient httpClient, Element articleElement, LocalDateTime crawlTime) {
        super(httpClient, articleElement, crawlTime);
    }

    @Override
    protected String assignImageSelectorValue() {
        return "p.article-thumbnail > a > img";
    }

    @Override
    protected String assignTitleSelectorValue() {
        return "header > p.article-title > a";
    }

    @Override
    protected String assignDescriptionSelectorValue() {
        return "article > section.main > p.the-article-summary";
    }

    @Override
    protected String assignBodySelectorValue() {
        return "article > section.main > div.the-article-body";
    }

    @Override
    protected String assignPublishDateSelectorValue() {
        return "article > header > ul > li.the-article-publish";
    }

    @Override
    protected ArticleAttributes.NewsPage assignPage() {
        return ArticleAttributes.NewsPage.ZINGNEWS;
    }

    @Override
    protected String assignRegexId() {
        return "^.+-post(\\d+).html?$";
    }

    @Override
    protected LocalDateTime getPublishDate(Document articleDocument) {
        Pattern pattern = Pattern.compile("^.*,(.*)\\s\\(GMT\\+\\d\\)$");
        Element publishDateElement = articleDocument.selectFirst(this.getPublishDateSelector());
        if (Objects.isNull(publishDateElement)) return null;
        Matcher matcher = pattern.matcher(publishDateElement.text());
        if (matcher.find()) {
            String publishDateStr = matcher.group(1);
            try {
                return LocalDateTime.parse(publishDateStr.trim(), FORMATTER);
            } catch (Exception ex) {
                log.error("Parse date error", ex);
            }
        }
        return null;
    }
}

