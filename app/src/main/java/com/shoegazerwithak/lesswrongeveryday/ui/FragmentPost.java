package com.shoegazerwithak.lesswrongeveryday.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shoegazerwithak.lesswrongeveryday.R;
import com.shoegazerwithak.lesswrongeveryday.constants.Constants;
import com.shoegazerwithak.lesswrongeveryday.model.Article;
import com.shoegazerwithak.lesswrongeveryday.utils.JsonCacheHelper;
import com.shoegazerwithak.lesswrongeveryday.utils.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentPost extends Fragment {
    protected RecyclerView mRecyclerViewPost;
    RecyclerView.Adapter recyclerViewAdapter;
    OkHttpClient client = new OkHttpClient();
    private ArtistsFragmentInteractionListener mListener;
    private List<Article> mData;

    public FragmentPost() {
    }

    public static FragmentPost newInstance() {
        return new FragmentPost();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("onResume", String.valueOf(mData));
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mData = filterReadArticles(mData);
//        recyclerViewAdapter = new RecyclerViewAdapter(mData, mListener);
//        mRecyclerViewPost.setAdapter(recyclerViewAdapter);
//        Log.d("onStart", String.valueOf(mData.size()));
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        mRecyclerViewPost = (RecyclerView) rootView.findViewById(R.id.recyclerview_post);

        // To avoid "No adapter attached; skipping layout"
        mData = new ArrayList<>();
        mRecyclerViewPost.setAdapter(new RecyclerViewAdapter(mData, mListener));
        runJsonParsingTask(); // Download JSON, parse it and configure RecyclerView to show it
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ArtistsFragmentInteractionListener) {
            mListener = (ArtistsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void runJsonParsingTask() {
        JsonDownloaderTask jsonDownloader = new JsonDownloaderTask();
        jsonDownloader.execute(Constants.API_ENDPOINT);
    }

    private void setupRecyclerView(List<Article> data) {
        mData = data;
        recyclerViewAdapter = new RecyclerViewAdapter(mData, mListener);
        mRecyclerViewPost.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewPost.setAdapter(recyclerViewAdapter);
    }

    public interface ArtistsFragmentInteractionListener {
//        void onListItemClick(Article artistItem, View view);
        void onListItemClick(Article artistItem);
    }

    /**
     * Downloads JSON file from specified URL.
     */
    private class JsonDownloaderTask extends AsyncTask<String, Integer, List<Article>> {
        @Override
        protected List<Article> doInBackground(String... params) {
            String json = JsonCacheHelper.getCachedJson(getContext(), Constants.CACHED_ARTICLES_LIST);
            JSONArray jsonArray;
            List<Article> articles = new ArrayList<>();

            try {
                jsonArray = new JSONArray(json);
                return JsonCacheHelper.getCachedArticles(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
                Request request = new Request.Builder()
                        // Possible NullPointerException...
                        .url(params[0])
                        .build();
                Response response;
                try {
                    response = client.newCall(request).execute();
                    String body = response.body().toString();
                    Document doc = Jsoup.parse(body, Constants.API_ENDPOINT);
                    Elements list = doc.select(Constants.LIST_SELECTOR);
                    JSONArray articlesJSON = new JSONArray();
                    JSONArray readArticles = JsonCacheHelper.getJsonArray(getContext());
                    for (Element el : list) {
                        String link = el.child(0).attr(Constants.HREF_SELECTOR);
                        if (JsonCacheHelper.indexOfJSONArray(readArticles, link) < 0) {
                            String title = el.text();
                            Article article = new Article(title, link);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put(Constants.ARTICLE_JSON_LINK, link);
                                jsonObject.put(Constants.ARTICLE_JSON_TITLE, title);
                            } catch (JSONException e3) {
                                e3.printStackTrace();
                            }
                            articlesJSON.put(jsonObject);
                            articles.add(article);
                        }
                    }
                    JsonCacheHelper.cacheJson(getContext(), articlesJSON.toString(), Constants.CACHED_ARTICLES_LIST);
                    return articles;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Article> result) {
            super.onPostExecute(result);
            setupRecyclerView(result);
        }
    }
}

