package com.shoegazerwithak.lesswrongeveryday.utils;

import android.content.Context;
import android.util.Log;

import com.shoegazerwithak.lesswrongeveryday.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

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

    public static void cacheJson(Context context, String jsonContents) {
        cacheJson(context, jsonContents, Constants.CACHED_FILE_NAME);
    }

    public static void appendToCachedArray(Context context, String element, String fileName, String key) {
        JSONArray jsonArray = getJsonArray(context, fileName, key);
        if (jsonArray != null) {
            jsonArray.put(element);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("list", jsonArray);
                Log.d("about to store", jsonObject.toString());
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
    public static String getCachedJson(Context context, String fileName) {
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
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            cacheJson(context, Constants.EMPTY_JSON_ARRAY, fileName);
            Log.d("Ein", Constants.EMPTY_JSON_ARRAY);
            return Constants.EMPTY_JSON_ARRAY;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCachedJson(Context context) {
        return getCachedJson(context, Constants.CACHED_FILE_NAME);
    }

    public static JSONArray getJsonArray(Context context, String fileName, String key) {
        String jsonString = getCachedJson(context, fileName);
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
