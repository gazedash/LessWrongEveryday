package com.shoegazerwithak.lesswrongeveryday.constants

object Constants {
    val API_ENDPOINT = "http://lesswrong.ru"
    val LIST_SELECTOR = ".leaf:not(.menu-depth-1)"
    val HREF_SELECTOR = "abs:href"
    val ARTICLE_TEXT_SELECTOR = ".field-items"
    val BUNDLE_ARTICLE_NAME = "article"
    val BUNDLE_NEXT_INDEX = "nextIndex"
    val CACHED_FILE_NAME = "articles.json"
    val CACHED_ARTICLES_LIST = "articlesCache.json"
    val CACHED_ARRAY_NAME = "list"
    val EMPTY_JSON_ARRAY = "{\"list\":[]}"
    val ARTICLE_JSON_LINK = "link"
    val ARTICLE_JSON_TITLE = "title"
    val NEXT_ARTICLE_FILENAME = "nextArticle.json"
}
