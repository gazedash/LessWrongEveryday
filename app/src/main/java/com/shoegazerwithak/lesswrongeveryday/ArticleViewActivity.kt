package com.shoegazerwithak.lesswrongeveryday

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView

import com.shoegazerwithak.lesswrongeveryday.constants.Constants
import com.shoegazerwithak.lesswrongeveryday.model.Article
import com.shoegazerwithak.lesswrongeveryday.utils.JsonCacheHelper

import okhttp3.OkHttpClient

class ArticleViewActivity : Activity() {
    private var client = OkHttpClient()

    private var titleView: TextView? = null
    private var fab: FloatingActionButton? = null
    private var articleView: TextView? = null
    private var article: Article? = null
    private var link: String? = ""
    private var title = ""
    private var nextTitle = ""

    private var fabOnClickListener: OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_view)
        val bundle = intent.extras
        article = bundle.get(Constants.BUNDLE_ARTICLE_NAME) as Article
        nextTitle = bundle.getString(Constants.BUNDLE_NEXT_INDEX)
//        if (article != null) {
        link = article!!.link
        title = article!!.title
//        }
        titleView = findViewById(R.id.article_title) as TextView
        fab = findViewById(R.id.button_article_done) as FloatingActionButton
        articleView = findViewById(R.id.article_view) as TextView
        titleView!!.text = title

        fabOnClickListener = fabListenerClass()
        fab!!.setOnClickListener(fabOnClickListener)

        if (link!!.isNotEmpty()) {
            htmlParseAndSetText.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, link)
        }
    }

    private val htmlParseAndSetText: AsyncTask<String, Int, String>
        get() = object : AsyncTask<String, Int, String>() {
            override fun doInBackground(vararg params: String): String? {
                val articleUrl = params[0]
                val fileName = JsonCacheHelper.getFileNameFromString(articleUrl)
                val body = JsonCacheHelper.getCachedJson(this@ArticleViewActivity, fileName, false)
                if (body != null) return body else return JsonCacheHelper.getArticleTextAndCache(this@ArticleViewActivity, client, link!!)
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (result != null) {
                    articleView!!.text = result
                    fab!!.visibility = View.VISIBLE
                }
            }
        }

    override fun onBackPressed() {
        super.onBackPressed()
        setResultAndFinish(Activity.RESULT_CANCELED, false)
    }

    internal inner class fabListenerClass : OnClickListener {
        override fun onClick(v: View) {
            JsonCacheHelper.appendToCachedArray(v.context, link!!)
            JsonCacheHelper.cacheJson(v.context, nextTitle, Constants.NEXT_ARTICLE_FILENAME)
            setResultAndFinish(Activity.RESULT_OK, true)
        }
    }

    private fun setResultAndFinish(resultCode: Int, result: Boolean?) {
        val resultIntent = Intent()
        resultIntent.putExtra("Filter", result)
        setResult(resultCode, resultIntent)
        finish()
    }
}
