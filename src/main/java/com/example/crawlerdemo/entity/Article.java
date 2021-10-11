package com.example.crawlerdemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Document(indexName = "article")
@Setting(shards = 5, replicas = 0)
@TypeAlias("article")
@Getter
@Setter
@ToString
public class Article {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String image;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String body;

    @Field(type = FieldType.Keyword)
    private String source;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "publish_date")
    private LocalDateTime publishDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis, name = "crawl_date")
    private LocalDateTime crawlDate;
}
