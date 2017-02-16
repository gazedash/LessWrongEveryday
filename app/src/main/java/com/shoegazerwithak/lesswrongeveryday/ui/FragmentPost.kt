package com.shoegazerwithak.lesswrongeveryday.ui

import android.app.Activity
import android.app.Fragment
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.shoegazerwithak.lesswrongeveryday.R
import com.shoegazerwithak.lesswrongeveryday.constants.Constants
import com.shoegazerwithak.lesswrongeveryday.model.Article
import com.shoegazerwithak.lesswrongeveryday.utils.JsonCacheHelper
import com.shoegazerwithak.lesswrongeveryday.utils.RecyclerViewAdapter

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.util.ArrayList

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class FragmentPost : Fragment() {
    private var mRecyclerViewPost: RecyclerView? = null
    private var recyclerViewAdapter: RecyclerView.Adapter<*>? = null
    private var client = OkHttpClient()
    private var mListener: ArtistsFragmentInteractionListener? = null

    private var mData: List<Article>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        Log.d("oncReateView", "fdsfadsfa")
        val rootView = inflater.inflate(R.layout.fragment_post, container, false)
        mRecyclerViewPost = rootView.findViewById(R.id.recyclerview_post) as RecyclerView
        // To avoid "No adapter attached; skipping layout"
        // TODO:check delete initializer
        mData = ArrayList<Article>()
        Log.d("onCreateView", (mListener != null).toString())
        mRecyclerViewPost!!.adapter = RecyclerViewAdapter(mData!!, mListener)
        runJsonParsingTask() // Download JSON, parse it and configure RecyclerView to show it
        return rootView
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Log.d("onAttach", (activity is ArtistsFragmentInteractionListener).toString())
        if (activity is ArtistsFragmentInteractionListener) {
            mListener = activity
        } else {
            throw RuntimeException(activity.toString() + " must implement ArtistsFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("onDeatch?", (mListener != null).toString())
        mListener = null
    }

    fun runJsonParsingTask() {
        val jsonDownloader = JsonDownloaderTask()
        jsonDownloader.execute(Constants.API_ENDPOINT)
    }

    fun cacheArticles() {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                val articlesJson = JsonCacheHelper.getJsonArray(activity)
                if (articlesJson!!.length() == 0) {
                    for (article in mData!!) {
                        JsonCacheHelper.getArticleTextAndCache(activity, client, article.link)
                    }
                }
                return null
            }
        }.execute()
    }

    private fun setupRecyclerView(data: List<Article>) {
        mData = filterReadArticles(data)
        Log.d("mData size", mData!!.size.toString())
        Log.d("setupRecyclerView", (mListener != null).toString())
        recyclerViewAdapter = RecyclerViewAdapter(mData!!, mListener)
        mRecyclerViewPost!!.layoutManager = LinearLayoutManager(activity)
        mRecyclerViewPost!!.adapter = recyclerViewAdapter
    }

    fun doneCallback() {
        mData = filterReadArticles(mData!!)
        mRecyclerViewPost!!.adapter = RecyclerViewAdapter(mData!!, mListener)
    }

    private fun filter(articles: List<Article>?, predicate: Callable<Article, Boolean>): List<Article> {
        return articles!!.filter { predicate.call(it) }
    }

    private fun filterReadArticles(articles: List<Article>): List<Article> {
        return filter(articles, object : Callable<Article, Boolean> {
            override fun call(input: Article): Boolean {
                val res = JsonCacheHelper.indexOfJSONArray(JsonCacheHelper.getJsonArray(activity)!!, input.link)
                return res < 0
            }
        })
    }

    private fun getArticles(link: String?): List<Article>? {
        val request = Request.Builder()
                .url(link)
                .build()
        val response: Response
        val articles = ArrayList<Article>()
        try {
            response = client.newCall(request).execute()
            val body = response.body().string()
            val doc = Jsoup.parse(body, Constants.API_ENDPOINT)
            val list = doc.select(Constants.LIST_SELECTOR)
            val articlesJSON = JSONArray()
            val readArticles = JsonCacheHelper.getJsonArray(activity)
            for (el in list) {
                val articleLink = el.child(0).attr(Constants.HREF_SELECTOR)
                if (JsonCacheHelper.indexOfJSONArray(readArticles!!, articleLink) < 0) {
                    val title = el.text()
                    val article = Article(title, articleLink)
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put(Constants.ARTICLE_JSON_LINK, articleLink)
                        jsonObject.put(Constants.ARTICLE_JSON_TITLE, title)
                    } catch (e3: JSONException) {
                        e3.printStackTrace()
                    }
                    articlesJSON.put(jsonObject)
                    articles.add(article)
                }
            }
            JsonCacheHelper.cacheJson(activity, articlesJSON.toString(), Constants.CACHED_ARTICLES_LIST)
            return articles
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return null
    }

    interface ArtistsFragmentInteractionListener {
        fun onListItemClick(artistItem: Article, nextTitle: String)
        //        void onListItemClick(Article artistItem);
        fun onLoadingFail()

        fun onOfflineModeEnabled(enabled: Boolean, performConnectivityCheck: Boolean)
    }

    interface Callable<in I, out Boolean> {
        fun call(input: I): Boolean
    }

    /**
     * Downloads JSON file from specified URL.
     */
    private inner class JsonDownloaderTask : AsyncTask<String, Int, List<Article>>() {
        override fun doInBackground(vararg params: String): List<Article>? {
            val json = JsonCacheHelper.getCachedJson(activity, Constants.CACHED_ARTICLES_LIST, true)
            val link = params[0]
            val jsonArray: JSONArray
            if (json!!.length > 11) {
                try {
                    jsonArray = JSONArray(json)
                    return JsonCacheHelper.jsonToArticleList(jsonArray)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return getArticles(link)
        }

        override fun onPostExecute(articles: List<Article>?) {
            super.onPostExecute(articles)
            if (articles != null) {
                setupRecyclerView(articles)
                cacheArticles()
            } else {
                // No connection, no cached data - show an error
                if (mListener != null) mListener!!.onLoadingFail()
            }
        }
    }

    companion object {
        fun newInstance(): FragmentPost {
            return FragmentPost()
        }
    }
}