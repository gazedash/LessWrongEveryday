package com.shoegazerwithak.lesswrongeveryday;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.model.Article;
import com.shoegazerwithak.lesswrongeveryday.utils.JsonCacheHelper;

import okhttp3.OkHttpClient;

public class ArticleViewActivity extends Activity {
    OkHttpClient client = new OkHttpClient();

    TextView titleView;
    FloatingActionButton fab;
    TextView articleView;
    Article article;
    String link = "";
    String title = "";

    OnClickListener fabOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        Bundle bundle = getIntent().getExtras();
        article = (Article) bundle.get(Constants.BUNDLE_ARTICLE_NAME);

        if (article != null) {
            link = article.link;
            title = article.title;
        }

        titleView = (TextView) findViewById(R.id.article_title);
        fab = (FloatingActionButton) findViewById(R.id.button_article_done);
        articleView = (TextView) findViewById(R.id.article_view);
        titleView.setText(title);

        fabOnClickListener = new fabListenerClass();
        fab.setOnClickListener(fabOnClickListener);

        if (link != null && link.length() > 0) {
            getHtmlParseAndSetText().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, link);
        }
    }

    @NonNull
    private AsyncTask<String, Integer, String> getHtmlParseAndSetText() {
        return new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                String articleUrl = params[0];
                String fileName = JsonCacheHelper.getFileNameFromString(articleUrl);
                String body = JsonCacheHelper.getCachedJson(ArticleViewActivity.this, fileName, false);
                if (body != null) {
                    return body;
                } else {
                    return JsonCacheHelper.getArticleTextAndCache(ArticleViewActivity.this, client, link);
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    articleView.setText(result);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultAndFinish(RESULT_CANCELED, false);
    }

    class fabListenerClass implements OnClickListener {
        public void onClick(View v) {
            JsonCacheHelper.appendToCachedArray(v.getContext(), link);
            setResultAndFinish(RESULT_OK, true);
        }
    }

    private void setResultAndFinish(int resultCode, Boolean result) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("Filter", result);
        setResult(resultCode, resultIntent);
        finish();
    }
}
