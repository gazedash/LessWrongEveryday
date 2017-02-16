package com.shoegazerwithak.lesswrongeveryday.utils

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.shoegazerwithak.lesswrongeveryday.R
import com.shoegazerwithak.lesswrongeveryday.model.Article
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost

class RecyclerViewAdapter(private val SubjectValues: List<Article>, private val mListener: FragmentPost.ArtistsFragmentInteractionListener?) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    init {
        Log.d("recycleviewadapter", (mListener != null).toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val view1 = LayoutInflater.from(parent.context).inflate(R.layout.item_fragment, parent, false)
        return ViewHolder(view1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mMap = SubjectValues[position]
        holder.articleName.text = mMap.title
    }

    override fun getItemCount(): Int {
        return SubjectValues.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var articleName: TextView = view.findViewById(R.id.article_name) as TextView

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val index = adapterPosition
            val nextIndex = index + 1
            val clicked = SubjectValues[index]
            val nextTitle = if (SubjectValues.size > nextIndex) SubjectValues[nextIndex].title else "nothing"
            Log.d("clicked title", clicked.title)
            Log.d("nextTitle", nextTitle)
            Log.d("mListener", (mListener != null).toString())
            mListener!!.onListItemClick(clicked, nextTitle)
            //            mListener.onListItemClick(clicked);
        }
    }
}