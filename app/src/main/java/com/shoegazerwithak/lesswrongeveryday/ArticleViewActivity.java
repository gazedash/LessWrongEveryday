package com.shoegazerwithak.lesswrongeveryday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class ArticleViewActivity extends Activity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcticle_view);
//        context = getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        String link = bundle.getString("link");
        String text = bundle.getString("text");

        Intent incoming = getIntent();

        textView = (TextView) findViewById(R.id.article_view);
        textView.setText(text);
        Log.d("Context", link);
        Log.d("Context", text);
        Log.d("Context", String.valueOf(incoming.getExtras()));
    }
}
