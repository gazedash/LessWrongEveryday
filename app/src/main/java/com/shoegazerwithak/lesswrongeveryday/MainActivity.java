package com.shoegazerwithak.lesswrongeveryday;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;

import com.shoegazerwithak.lesswrongeveryday.utils.RecyclerViewAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    Context context;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyсlerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);

        recyсlerViewLayoutManager = new LinearLayoutManager(context);

        recyclerView.setLayoutManager(recyсlerViewLayoutManager);

        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                Request request = new Request.Builder()
                        .url("http://lesswrong.ru/")
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Document doc = Jsoup.parse(result, "https://lesswrong.ru");
                Elements list = doc.select(".leaf:not(.menu-depth-1)");
                List<Map<String, String>> links = new ArrayList<>();
                for (Element el : list) {
                    String link = el.child(0).attr("abs:href");
                    Map<String, String> mMap = new HashMap<>();
                    mMap.put("text", el.text());
                    mMap.put("link", link);
                    links.add(mMap);
                }
                Log.e("ANSWER", "" + links);
                recyclerViewAdapter = new RecyclerViewAdapter(context, links);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }.execute();
    }
}
