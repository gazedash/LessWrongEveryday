package com.shoegazerwithak.lesswrongeveryday;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.utils.MyAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    // this method is called from layout (activity_main)
    class GetImageTask extends AsyncTask<String, Integer, String> {
        private String link;
        private final MyAdapter.ViewHolder holder;

        public GetImageTask(String link, MyAdapter.ViewHolder holder) {
            this.link = link;
            this.holder = holder;
        }

        @Override
        protected String doInBackground(String... params) {
            Request request = new Request.Builder()
                    .url("http://lesswrong.ru/")
                    .build();
            // TODO Auto-generated method stub
            Response response = null;
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
            List<String> links = new ArrayList<>();
            for (Element el : list) {
                String link = el.child(0).attr("abs:href");
                links.add(link);
            }
            Log.e("ANSWER", "" + links);
        }
    }
}
