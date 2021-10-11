package com.example.crawlerdemo.crawler.vnexpress;

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
public class VnExpressArticleCrawler extends BaseArticleCrawler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d/MM/yyyy, HH:mm");

    protected VnExpressArticleCrawler(OkHttpClient httpClient, Element articleElement, LocalDateTime crawlTime) {
        super(httpClient, articleElement, crawlTime);
    }

    @Override
    protected String assignImageSelectorValue() {
        return "div.thumb-art > a  > picture > img";
    }

    @Override
    protected String assignTitleSelectorValue() {
        return "h3.title-news > a";
    }

    @Override
    protected String assignDescriptionSelectorValue() {
        return "section.section.page-detail.top-detail > div > div.sidebar-1 > p.description";
    }

    @Override
    protected String assignBodySelectorValue() {
        return "section.section.page-detail.top-detail > div > div.sidebar-1 > article.fck_detail";
    }

    @Override
    protected String assignPublishDateSelectorValue() {
        return "section.section.page-detail.top-detail > div > div.sidebar-1 > div.header-content.width_common > span.date";
    }

    @Override
    protected ArticleAttributes.NewsPage assignPage() {
        return ArticleAttributes.NewsPage.VNEXPRESS;
    }

    @Override
    protected String assignRegexId() {
        return "^.+-(\\d+).html?$";
    }

    @Override
    protected LocalDateTime getPublishDate(Document articleDocument) {
        Pattern pattern = Pattern.compile("^.*,(\\s.*,\\s.*)\\s\\(GMT\\+\\d\\)$");
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
