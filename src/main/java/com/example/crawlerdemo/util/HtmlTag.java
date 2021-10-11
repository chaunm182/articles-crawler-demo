package com.example.crawlerdemo.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HtmlTag {
    public static final String A_TAG = "a";


    @UtilityClass
    public class Property{
        public static final String HREF = "href";
        public static final String SOURCE = "src";
        public static final String DATA_SOURCE = "data-src";
    }
}
