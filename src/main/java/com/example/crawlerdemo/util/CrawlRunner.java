package com.example.crawlerdemo.util;

import com.example.crawlerdemo.crawler.dantri.DanTriCrawler;
import com.example.crawlerdemo.crawler.dantri.DanTriScheduleCrawler;
import com.example.crawlerdemo.crawler.vnexpress.VnExpressCrawler;
import com.example.crawlerdemo.crawler.vnexpress.VnExpressScheduleCrawler;
import com.example.crawlerdemo.crawler.zingnews.ZingNewsScheduleCrawler;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
public class CrawlRunner implements ApplicationRunner {

    private final RestHighLevelClient restHighLevelClient;
    private final VnExpressCrawler vnExpressCrawler;
    private final DanTriCrawler danTriCrawler;
    private final ZingNewsScheduleCrawler zingNewsScheduleCrawler;
    private final DanTriScheduleCrawler danTriScheduleCrawler;
    private final VnExpressScheduleCrawler vnExpressScheduleCrawler;

    @Override
    public void run(ApplicationArguments args) throws Exception {

//        ExecutorService crawlPool = Executors.newFixedThreadPool(3);
//        crawlPool.execute(danTriCrawler);
//        crawlPool.shutdown();
//        zingNewsScheduleCrawler.scheduleCrawlTask();
        danTriScheduleCrawler.scheduleCrawlTask();
//        vnExpressScheduleCrawler.scheduleCrawlTask();
    }


}
