package com.shoegazerwithak.lesswrongeveryday;

import android.content.Context;
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
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.utils.RecyclerViewAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    Context context;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    String[] subjects =
            {
                    "ANDROID",
                    "PHP",
                    "BLOGGER",
                    "WORDPRESS",
                    "JOOMLA",
                    "ASP.NET",
                    "JAVA",
                    "C++",
                    "MATHS",
                    "HINDI",
                    "ENGLISH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview1);

        recylerViewLayoutManager = new LinearLayoutManager(context);

        recyclerView.setLayoutManager(recylerViewLayoutManager);

        new AsyncTask<String, Integer, String>() {
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
                recyclerViewAdapter = new RecyclerViewAdapter(context, links);
                recyclerView.setAdapter(recyclerViewAdapter);

            }
        }.execute();

//        recyclerViewAdapter = new RecyclerViewAdapter(context, subjects);
//
//        recyclerView.setAdapter(recyclerViewAdapter);
    }

    // this method is called from layout (activity_main)
//    public void showShortToast(View view) {
//        new AsyncTask<String, Integer, String>() {
//            @Override
//            protected String doInBackground(String... params) {
//                Request request = new Request.Builder()
//                        .url("http://lesswrong.ru/")
//                        .build();
//                // TODO Auto-generated method stub
//                Response response = null;
//                try {
//                    response = client.newCall(request).execute();
//                    return response.body().string();
//                } catch (Exception e) {
//                    // TODO: handle exception
//                    e.printStackTrace();
//                }
//                return null;
//            }
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//                TextView text = (TextView) findViewById(R.id.subject_textview);
//                Document doc = Jsoup.parse(result, "https://lesswrong.ru");
//                Elements list = doc.select(".leaf:not(.menu-depth-1)");
//                List<String> links = new ArrayList<>();
//                for (Element el : list) {
//                    String link = el.child(0).attr("abs:href");
//                    links.add(link);
//                }
//                text.setMovementMethod(new ScrollingMovementMethod());
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    text.setText(Html.fromHtml(String.valueOf(links), Html.FROM_HTML_MODE_LEGACY));
//                } else {
//                    text.setText(Html.fromHtml(String.valueOf(links)));
//                }
//                Log.e("ANSWER", "" + links);
//            }
//        }.execute();
//    }
}
