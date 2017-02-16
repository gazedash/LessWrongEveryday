package com.shoegazerwithak.lesswrongeveryday.utils

import android.content.Context
import android.util.Base64
import android.util.Log

import com.shoegazerwithak.lesswrongeveryday.constants.Constants
import com.shoegazerwithak.lesswrongeveryday.model.Article

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object JsonCacheHelper {
    /**
     * Saves a file with JSON code into internal storage

     * @param jsonContents json code
     */
    @JvmOverloads fun cacheJson(context: Context, jsonContents: String, fileName: String = Constants.CACHED_FILE_NAME) {
        val outputStream: FileOutputStream
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            outputStream.write(jsonContents.toByteArray())
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(JSONException::class)
    fun jsonToArticleList(jsonArray: JSONArray): List<Article> {
        val articles = ArrayList<Article>()
        (0..jsonArray.length() - 1).forEach { i ->
            val jsonArticle = jsonArray.getJSONObject(i)
            val link = jsonArticle.getString(Constants.ARTICLE_JSON_LINK)
            if (indexOfJSONArray(jsonArray, link) < 0) {
                val title = jsonArticle.getString(Constants.ARTICLE_JSON_TITLE)
                val article = Article(title, link)
                articles.add(article)
            }
        }
        Log.d("articles", articles.size.toString())
        return articles
    }

    fun indexOfJSONArray(arr: JSONArray, element: Any): Int {
        try {
            (0..arr.length() - 1)
                    .filter { element.toString() == arr.getString(it) }
                    .forEach { return it }
        } catch (e: JSONException) {
            e.printStackTrace()
            return -1
        }
        return -1
    }

    @JvmOverloads fun appendToCachedArray(context: Context, element: String, fileName: String = Constants.CACHED_FILE_NAME, key: String = Constants.CACHED_ARRAY_NAME) {
        val jsonArray = getJsonArray(context, fileName, key)
        Log.d("jsonArray != null", (jsonArray?.length() ?: 0).toString())
        if (jsonArray != null && indexOfJSONArray(jsonArray, element) < 0) {
            jsonArray.put(element)
            val jsonObject = JSONObject()
            try {
                jsonObject.put(key, jsonArray)
                cacheJson(context, jsonObject.toString(), fileName)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Read the file with JSON code from internal storage
     * @return JSON code string
     * * The snippet is taken from [here](http://www.stackoverflow.com/questions/14768191/how-do-i-read-the-file-content-from-the-internal-storage-android-app)
     */
    @JvmOverloads fun getCachedJson(context: Context, fileName: String = Constants.CACHED_FILE_NAME, isArray: Boolean? = true): String? {
        val inputStream: FileInputStream
        val inputStreamReader: InputStreamReader
        val bufferedReader: BufferedReader
        try {
            inputStream = context.openFileInput(fileName)
            inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String? = null
            while ({ line = bufferedReader.readLine(); line }() != null) stringBuilder.append(line)
            return stringBuilder.toString()
        } catch (e: Exception) {
            when(e) {
                is FileNotFoundException -> {
                    e.printStackTrace()
                    if (isArray!!) {
                        cacheJson(context, Constants.EMPTY_JSON_ARRAY, fileName)
                        return Constants.EMPTY_JSON_ARRAY
                    } else return null
                }
                is IOException -> {
                    e.printStackTrace()
                    return null
                }
                else -> throw e
            }
        }


    }

    fun getTextFromBody(body: String): String {
        val doc = Jsoup.parse(body, Constants.API_ENDPOINT)
        val textNode = doc.select(Constants.ARTICLE_TEXT_SELECTOR)
        return textNode.text()
    }

    fun sha1(input: String): String {
        val mDigest: MessageDigest
        try {
            mDigest = MessageDigest.getInstance("SHA1")
            val result = mDigest.digest(input.toByteArray())
            val sb = StringBuilder()
            result.forEach { aResult -> sb.append(Integer.toString((aResult.toInt() and 0xff) + 0x100, 16).substring(1)) }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return input
        }
    }

    fun getFileNameFromString(str: String): String = String(Base64.encode(sha1(str).substring(10).replace("\\W+".toRegex(), "").toByteArray(Charset.forName("UTF-8")), Base64.DEFAULT))
    //        return new String(Base64.encode(str.substring(10).replaceAll("\\W+", "").getBytes(Charset.forName("UTF-8")), Base64.DEFAULT));

    fun getArticleTextAndCache(context: Context, client: OkHttpClient, link: String): String? {
        val fileName = getFileNameFromString(link)
        val f = File(link)
        if (f.exists() && !f.isDirectory) return JsonCacheHelper.getCachedJson(context, fileName, false)
        val requestText = Request.Builder()
                .url(link)
                .build()
        try {
            val responseText = client.newCall(requestText).execute()
            var body = responseText.body().string()
            body = JsonCacheHelper.getTextFromBody(body)
            JsonCacheHelper.cacheJson(context, body, fileName)
            return body
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    @JvmOverloads fun getJsonArray(context: Context, fileName: String = Constants.CACHED_FILE_NAME, key: String = Constants.CACHED_ARRAY_NAME): JSONArray? {
        val jsonString = getCachedJson(context, fileName, true)
        try {
            return JSONObject(jsonString).getJSONArray(key)
        } catch (e: Exception) {
            when(e) {
                is JSONException, is NullPointerException -> {
                    cacheJson(context, Constants.EMPTY_JSON_ARRAY, fileName)
                    e.printStackTrace()
                    return JSONArray(Constants.EMPTY_JSON_ARRAY)
                }
                else -> throw e
            }
        }
    }
}