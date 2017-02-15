package com.shoegazerwithak.lesswrongeveryday.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.R;
import com.shoegazerwithak.lesswrongeveryday.model.Article;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final FragmentPost.ArtistsFragmentInteractionListener mListener;
    private List<Article> SubjectValues;

    public RecyclerViewAdapter(List<Article> SubjectValues1, FragmentPost.ArtistsFragmentInteractionListener listener) {
        SubjectValues = SubjectValues1;
        Log.d("recycleviewadapter", String.valueOf(listener != null));
        mListener = listener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment, parent, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article mMap = SubjectValues.get(position);
        holder.articleName.setText(mMap.title);
    }

    @Override
    public int getItemCount() {
        return SubjectValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView articleName;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            articleName = (TextView) view.findViewById(R.id.article_name);
        }

        @Override
        public void onClick(View view) {
//            mListener.onListItemClick(SubjectValues.get(getAdapterPosition()), view);
            int index = getAdapterPosition();
            int nextIndex = index + 1;
            Article clicked = SubjectValues.get(index);
            String nextTitle = (SubjectValues.size() > nextIndex) ? SubjectValues.get(nextIndex).title : "nothing";
            Log.d("clicked title", clicked.title);
            Log.d("nextTitle", nextTitle);
            Log.d("mListener", String.valueOf(mListener != null));
            mListener.onListItemClick(clicked, nextTitle);
        }
    }
}