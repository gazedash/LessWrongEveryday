package com.shoegazerwithak.lesswrongeveryday;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleViewActivity extends Activity {
    OkHttpClient client = new OkHttpClient();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        Bundle bundle = getIntent().getExtras();
        String link = bundle.getString("link");
        String text = bundle.getString("text");

        textView = (TextView) findViewById(R.id.article_view);
        textView.setText(text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        new AsyncTask<String, Integer, String>(){
            @Override
            protected String doInBackground(String... params) {
                Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Document doc = Jsoup.parse(result, "http://lesswrong.ru");
                Elements list = doc.select(".field-items");
                textView.setText(list.text());
            }
        }.execute(link);
    }
}
