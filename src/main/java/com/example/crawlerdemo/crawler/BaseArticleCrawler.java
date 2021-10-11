package com.example.crawlerdemo.crawler;

import com.example.crawlerdemo.entity.Article;
import com.example.crawlerdemo.exception.CrawlerException;
import com.example.crawlerdemo.util.ArticleAttributes;
import com.example.crawlerdemo.util.HtmlTag;
import com.example.crawlerdemo.util.StringsUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
public abstract class BaseArticleCrawler implements Supplier<Article> {

    private final OkHttpClient httpClient;

    private final Element articleElement;

    private final ArticleAttributes.NewsPage newsPage;

    private final String imageSelector;

    private final String titleSelector;

    private final String descriptionSelector;

    private final String bodySelector;

    private final String publishDateSelector;

    private final String regexId;

    private final LocalDateTime crawlTime;

    protected BaseArticleCrawler(OkHttpClient httpClient, Element articleElement, LocalDateTime crawlTime) {
        this.httpClient = httpClient;
        this.articleElement = articleElement;
        this.imageSelector = assignImageSelectorValue();
        this.titleSelector = assignTitleSelectorValue();
        this.descriptionSelector = assignDescriptionSelectorValue();
        this.bodySelector = assignBodySelectorValue();
        this.publishDateSelector = assignPublishDateSelectorValue();
        this.newsPage = assignPage();
        this.regexId = assignRegexId();
        this.crawlTime = crawlTime;
    }

    protected abstract String assignImageSelectorValue();

    protected abstract String assignTitleSelectorValue();

    protected abstract String assignDescriptionSelectorValue();

    protected abstract String assignBodySelectorValue();

    protected abstract String assignPublishDateSelectorValue();

    protected abstract ArticleAttributes.NewsPage assignPage();

    protected abstract String assignRegexId();

    protected abstract LocalDateTime getPublishDate(Document articleDocument);

    private boolean isValidTitleSelector(Element titleElement) {
        return titleElement.hasAttr(HtmlTag.Property.HREF);
    }


    private Document getArticleDocument(String articleUrl) throws IOException {
        Request request = new Request.Builder()
                .url(articleUrl)
                .build();
        Response response = httpClient.newCall(request).execute();
        if (response.code() != 200) {
            log.warn("Response code:{}", response.code());
            throw new CrawlerException("Error when connect to article url");
        }
        ResponseBody responseBody = response.body();
        if (Objects.isNull(responseBody)) throw new CrawlerException("Error when parse html body");
        String html = responseBody.string();
        return Jsoup.parse(html);
    }

    /*
     * Override this method in subclass if need
     * */
    protected String getBody(Document articleDocument) {
        Element contentElement = articleDocument.selectFirst(bodySelector);
        if (Objects.nonNull(contentElement)) return contentElement.text();
        return null;
    }

    /*
     * Override this method in subclass if need
     * */
    protected String getId(String articleUrl) {
        Pattern pattern = Pattern.compile(regexId);
        Matcher matcher = pattern.matcher(articleUrl);
        if (matcher.find()) {
            String id = matcher.group(1);
            return newsPage.toString().toUpperCase().concat(id);
        }
        return null;
    }


    @Override
    public Article get() {
        try {
            Article article = new Article();
            String articleUrl = null;

            //set title
            Element titleElement = articleElement.selectFirst(titleSelector);
            if (Objects.nonNull(titleElement) && isValidTitleSelector(titleElement)) {
                article.setTitle(titleElement.text());
                articleUrl = titleElement.attr(HtmlTag.Property.HREF);
                if (articleUrl.equals(StringsUtil.EMPTY)) throw new CrawlerException("Article url not found");
                else if (!articleUrl.startsWith("http")) articleUrl = newsPage.home.concat(articleUrl);
            }

            //set image
            Element imageElement = articleElement.selectFirst(imageSelector);
            if (Objects.nonNull(imageElement)) {
                if (imageElement.hasAttr(HtmlTag.Property.DATA_SOURCE))
                    article.setImage(imageElement.attr(HtmlTag.Property.DATA_SOURCE));
                else if (imageElement.hasAttr(HtmlTag.Property.SOURCE))
                    article.setImage(imageElement.attr(HtmlTag.Property.SOURCE));
            }

            //connect to article url
            Document articleDocument = getArticleDocument(articleUrl);

            //check and set publish date
            LocalDateTime articlePublishTime = getPublishDate(articleDocument);
            if (Objects.nonNull(articlePublishTime)) {
                if (articlePublishTime.isBefore(crawlTime)) return null;
                else article.setPublishDate(articlePublishTime);
            }

            //set description
            Element descriptionElement = articleDocument.selectFirst(descriptionSelector);
            if (Objects.nonNull(descriptionElement)) article.setDescription(descriptionElement.text());

            //set body
            article.setBody(getBody(articleDocument));

            //set source
            article.setSource(newsPage.home);

            //set id
            article.setId(getId(articleUrl));

            //audit
            article.setCrawlDate(LocalDateTime.now());

            return article;
        } catch (IOException ex) {
            throw new CrawlerException("Connection error", ex);
        }
    }
}
