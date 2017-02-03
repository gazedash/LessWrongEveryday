package com.shoegazerwithak.lesswrongeveryday;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.model.Article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleViewActivity extends Activity {
    OkHttpClient client = new OkHttpClient();

    TextView titleView;
    FloatingActionButton fab;
    TextView articleView;
    Article article;
    String link;
    String title;

    OnClickListener fabOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        Bundle bundle = getIntent().getExtras();
        article = (Article) bundle.get("article");
        link = article != null ? article.link : "";
        title = article != null ? article.title : "";

        titleView = (TextView) findViewById(R.id.article_title);
        fab = (FloatingActionButton) findViewById(R.id.button_article_done);
        articleView = (TextView) findViewById(R.id.article_view);
        titleView.setText(title);

        fabOnClickListener = new fabListenerClass();
        fab.setOnClickListener(fabOnClickListener);

        getHtmlParseAndSetText().execute(link);
    }

    @NonNull
    private AsyncTask<String, Integer, String> getHtmlParseAndSetText() {
        return new AsyncTask<String, Integer, String>() {
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
                Elements textNode = doc.select(".field-items");
                articleView.setText(textNode.text());
                fab.setVisibility(View.VISIBLE);
            }
        };
    }

    class fabListenerClass implements OnClickListener {
        public void onClick(View v) {
            Log.d("Fab click", link);
        }
    }
}
