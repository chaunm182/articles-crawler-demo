package com.example.crawlerdemo.exception;

public class CrawlerException extends RuntimeException{

    public CrawlerException(String s) {
        super(s);
    }

    public CrawlerException(Throwable throwable) {
        super(throwable);
    }

    public CrawlerException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
