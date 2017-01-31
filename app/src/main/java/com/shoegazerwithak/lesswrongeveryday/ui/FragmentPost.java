package com.shoegazerwithak.lesswrongeveryday.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shoegazerwithak.lesswrongeveryday.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentPost extends Fragment {
    @BindView(R.id.recyclerview1) RecyclerView recyclerView;

    protected RecyclerView mRecyclerViewArtists;
    OkHttpClient client = new OkHttpClient();
    private ArtistsFragmentInteractionListener mListener;
    private RecyclerViewAdapter mAdapter;
    private List<Map<String, String>> mData;

    public FragmentPost() {
    }

    public static FragmentPost newInstance() {
        return new FragmentPost();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        ButterKnife.bind(this, rootView);

//        getActivity().setTitle(R.string.app_name);

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
        jsonDownloader.execute("http://lesswrong.ru/");
    }

    private void setupRecyclerView(List<Map<String, String>> data) {
        mData = data;
        mAdapter = new RecyclerViewAdapter(data, mListener);
        mRecyclerViewArtists.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewArtists.setAdapter(mAdapter);
    }

    public interface ArtistsFragmentInteractionListener {
        void onListItemClick(Map<String, String> artistItem);
    }

    /**
     * Downloads JSON file from specified URL.
     */
    private class JsonDownloaderTask extends AsyncTask<String, Integer, String> {
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
            setupRecyclerView(links);
        }
    }
}

