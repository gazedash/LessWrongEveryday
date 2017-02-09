package com.shoegazerwithak.lesswrongeveryday.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shoegazerwithak.lesswrongeveryday.ArticleViewActivity;
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
import java.util.List;

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

    public void cacheArticles() {
        new AsyncTask<Void, Void, Void> () {
            @Override
            protected Void doInBackground(Void ...params) {
                JSONArray articlesJson = JsonCacheHelper.getJsonArray(getContext());
                if (articlesJson.length() == 0) {
                    for (Article article : mData) {
                        JsonCacheHelper.getArticleTextAndCache(getContext(), client, article.link);
                    }
                }
                return null;
            }
        }.execute();
    }

    private void setupRecyclerView(List<Article> data) {
        mData = filterReadArticles(data);
        Log.d("mData size", String.valueOf(mData.size()));
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
            String json = JsonCacheHelper.getCachedJson(getContext(), Constants.CACHED_ARTICLES_LIST, true);
            String link = params[0];
            JSONArray jsonArray;
            List<Article> articles = new ArrayList<>();
            if (json != null && json.length() > 11) {
                try {
                    jsonArray = new JSONArray(json);
                    return JsonCacheHelper.jsonToArticleList(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return getArticles(articles, link);
        }

        @Override
        protected void onPostExecute(List<Article> result) {
            super.onPostExecute(result);
            setupRecyclerView(result);
            cacheArticles();
        }
    }

    public void doneCallback() {
        mData = filterReadArticles(mData);
        mRecyclerViewPost.setAdapter(new RecyclerViewAdapter(mData, mListener));
    }

    public interface Callable<I, Boolean> {
        Boolean call(I input);
    }

    private List<Article> filter(List<Article> articles, Callable<Article, Boolean> predicate) {
        List<Article> res = new ArrayList<>();
        for (Article article : articles) {
                if (predicate.call(article)) {
                    res.add(article);
                }
        }
        return res;
    }

    private List<Article> filterReadArticles(List<Article> articles) {
        return filter(articles, new Callable<Article, Boolean>() {
            @Override
            public Boolean call(Article input) {
                int res = JsonCacheHelper.indexOfJSONArray(JsonCacheHelper.getJsonArray(getContext()), input.link);
                return res < 0;
            }
        });
    }

    @Nullable
    private List<Article> getArticles(List<Article> articles, String link) {
        Request request = new Request.Builder()
                // Possible NullPointerException...
                .url(link)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            String body = response.body().string();
            Document doc = Jsoup.parse(body, Constants.API_ENDPOINT);
            Elements list = doc.select(Constants.LIST_SELECTOR);
            JSONArray articlesJSON = new JSONArray();
            JSONArray readArticles = JsonCacheHelper.getJsonArray(getContext());
            for (Element el : list) {
                String articleLink = el.child(0).attr(Constants.HREF_SELECTOR);
                if (JsonCacheHelper.indexOfJSONArray(readArticles, articleLink) < 0) {
                    String title = el.text();
                    Article article = new Article(title, articleLink);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.ARTICLE_JSON_LINK, articleLink);
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

