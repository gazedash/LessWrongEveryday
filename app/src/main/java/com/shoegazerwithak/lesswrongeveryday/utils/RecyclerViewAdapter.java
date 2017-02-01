package com.shoegazerwithak.lesswrongeveryday.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shoegazerwithak.lesswrongeveryday.R;
import com.shoegazerwithak.lesswrongeveryday.ui.FragmentPost;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final FragmentPost.ArtistsFragmentInteractionListener mListener;
    private List<Map<String, String>> SubjectValues;

    public RecyclerViewAdapter(List<Map<String, String>> SubjectValues1, FragmentPost.ArtistsFragmentInteractionListener listener) {
        SubjectValues = SubjectValues1;
        mListener = listener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_article_view, parent, false);
        return new ViewHolder(view1);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, String> mMap = SubjectValues.get(position);
        holder.articleView.setText(mMap.get("text"));
    }

    @Override
    public int getItemCount() {
        return SubjectValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView articleView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            articleView = (TextView) view.findViewById(R.id.article_view);
        }

        @Override
        public void onClick(View view) {
            mListener.onListItemClick(SubjectValues.get(getAdapterPosition()));
        }
    }
}