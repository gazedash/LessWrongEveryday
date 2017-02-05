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

    private void setupRecyclerView(List<Article> data) {
        mData = data;
        recyclerViewAdapter = new RecyclerViewAdapter(mData, mListener);
        mRecyclerViewPost.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewPost.setAdapter(recyclerViewAdapter);
    }

    public interface ArtistsFragmentInteractionListener {
        void onListItemClick(Article artistItem, View view);

//        void onListItemClick(Article artistItem);
    }

    /**
     * Downloads JSON file from specified URL.
     */
    private class JsonDownloaderTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Request request = new Request.Builder()
                    // Possible NullPointerException...
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
            Document doc = Jsoup.parse(result, Constants.API_ENDPOINT);
            Elements list = doc.select(Constants.LIST_SELECTOR);
            List<Article> articles = new ArrayList<>();
            for (Element el : list) {
                String title = el.text();
                String link = el.child(0).attr(Constants.HREF_SELECTOR);
                Article article = new Article(title, link);
                articles.add(article);
            }
            setupRecyclerView(articles);
        }
    }
}

