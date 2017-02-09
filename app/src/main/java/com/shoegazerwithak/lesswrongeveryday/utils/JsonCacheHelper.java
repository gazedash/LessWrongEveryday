package com.shoegazerwithak.lesswrongeveryday.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.model.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class JsonCacheHelper {
    /**
     * Saves a file with JSON code into internal storage
     *
     * @param jsonContents json code
     */
    public static void cacheJson(Context context, String jsonContents, String fileName) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(jsonContents.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Article> jsonToArticleList(JSONArray jsonArray) throws JSONException {
        List <Article> articles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonArticle = jsonArray.getJSONObject(i);
            String link = jsonArticle.getString(Constants.ARTICLE_JSON_LINK);
            if (indexOfJSONArray(jsonArray, link) < 0) {
                String title = jsonArticle.getString(Constants.ARTICLE_JSON_TITLE);
                Article article = new Article(title, link);
                articles.add(article);
            } else {
                Log.d("hmm", "dont add");
            }
        }
        Log.d("articles", String.valueOf(articles.size()));
        return articles;
    }

    public static void cacheJson(Context context, String jsonContents) {
        cacheJson(context, jsonContents, Constants.CACHED_FILE_NAME);
    }

    public static int indexOfJSONArray(JSONArray arr, Object element) {
        try {
            for (int i = 0; i < arr.length(); i++) {
                if (element.toString().equals(arr.getString(i))) {
                    return i;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    public static void appendToCachedArray(Context context, String element, String fileName, String key) {
        JSONArray jsonArray = getJsonArray(context, fileName, key);
        Log.d("jsonArray != null", String.valueOf(jsonArray != null ? jsonArray.length() : 0));
        if (jsonArray != null && indexOfJSONArray(jsonArray, element) < 0) {
            jsonArray.put(element);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(key, jsonArray);
                cacheJson(context, jsonObject.toString(), fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void appendToCachedArray(Context context, String element) {
        appendToCachedArray(context, element, Constants.CACHED_FILE_NAME, Constants.CACHED_ARRAY_NAME);
    }

    /**
     * Read the file with JSON code from internal storage
     *
     * @return JSON code string
     * <p/>
     * The snippet is taken from <a href="http://www.stackoverflow.com/questions/14768191/how-do-i-read-the-file-content-from-the-internal-storage-android-app">here</a>
     */
    public static String getCachedJson(Context context, String fileName, Boolean isArray) {
        FileInputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        try {
            inputStream = context.openFileInput(fileName);
            inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (isArray) {
                cacheJson(context, Constants.EMPTY_JSON_ARRAY, fileName);
                return Constants.EMPTY_JSON_ARRAY;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTextFromBody(String body) {
        Document doc = Jsoup.parse(body, Constants.API_ENDPOINT);
        Elements textNode = doc.select(Constants.ARTICLE_TEXT_SELECTOR);
        return textNode.text();
    }

    public static String sha1(String input) {
        MessageDigest mDigest;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aResult : result) {
                sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return input;
        }
    }

    public static String getFileNameFromString(String str) {
//        return new String(Base64.encode(str.substring(10).replaceAll("\\W+", "").getBytes(Charset.forName("UTF-8")), Base64.DEFAULT));
        return new String(Base64.encode(sha1(str).substring(10).replaceAll("\\W+", "").getBytes(Charset.forName("UTF-8")), Base64.DEFAULT));
    }

    public static String getArticleTextAndCache(Context context, OkHttpClient client, String link) {
        String fileName = getFileNameFromString(link);
        File f = new File(link);
        if(f.exists() && !f.isDirectory()) {
            return JsonCacheHelper.getCachedJson(context, fileName, false);
        }
        String body;
        Request requestText = new Request.Builder()
                .url(link)
                .build();
        Response responseText;
        try {
            responseText = client.newCall(requestText).execute();
            body = responseText.body().string();
            body = JsonCacheHelper.getTextFromBody(body);
            JsonCacheHelper.cacheJson(context, body, fileName);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCachedJson(Context context) {
        return getCachedJson(context, Constants.CACHED_FILE_NAME, true);
    }

    public static JSONArray getJsonArray(Context context, String fileName, String key) {
        String jsonString = getCachedJson(context, fileName, true);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            return jsonObject.getJSONArray(key);
        } catch (JSONException | NullPointerException e) {
            cacheJson(context, Constants.EMPTY_JSON_ARRAY, fileName);
            e.printStackTrace();
            try {
                return new JSONArray(Constants.EMPTY_JSON_ARRAY);
            } catch (JSONException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    public static JSONArray getJsonArray(Context context) {
        return getJsonArray(context, Constants.CACHED_FILE_NAME, Constants.CACHED_ARRAY_NAME);
    }

}
