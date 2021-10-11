package com.example.crawlerdemo.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.Month;

@UtilityClass
public class ArticleAttributes {

    public static final String INDEX = "article";
    public static final LocalDateTime INIT_PUBLISH_TIME = LocalDateTime.of(2021, Month.OCTOBER,8,0,0,0); // 01/01/2021 00:00:00
    public static final int DEFAULT_NUMBER_OF_SHARD = 5;
    public static final int DEFAULT_NUMBER_OF_REPLICAS = 0;

    public enum NewsPage {
        DANTRI("https://dantri.com.vn"),
        VNEXPRESS("https://vnexpress.net"),
        ZINGNEWS("https://zingnews.vn");

        public final String home;

        NewsPage(String homePage) {
            this.home = homePage;
        }
    }
}
