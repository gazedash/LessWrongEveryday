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
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final FragmentPost.ArtistsFragmentInteractionListener mListener;
    private List<Article> SubjectValues;

    public RecyclerViewAdapter(List<Article> SubjectValues1, FragmentPost.ArtistsFragmentInteractionListener listener) {
        SubjectValues = SubjectValues1;
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
            mListener.onListItemClick(SubjectValues.get(getAdapterPosition()));
        }
    }
}