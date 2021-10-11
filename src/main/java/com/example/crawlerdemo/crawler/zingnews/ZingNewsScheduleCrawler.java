package com.example.crawlerdemo.crawler.zingnews;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
@Slf4j
public class ZingNewsScheduleCrawler {

    private final ZingNewsCrawler crawler;

    public void scheduleCrawlTask(){
        Set<String> categoriesUrl = crawler.getAllUrlCategories();
        System.out.println(categoriesUrl);
    }
}
