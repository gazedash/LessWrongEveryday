package com.shoegazerwithak.lesswrongeveryday.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.shoegazerwithak.lesswrongeveryday.R;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    List<Map<String, String>> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;
    
    private AdapterView.OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(Context context1, List<Map<String, String>> SubjectValues1, AdapterView.OnItemClickListener onItemClickListener) {
        SubjectValues = SubjectValues1;
        context = context1;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items, parent, false);
        view1.setOnClickListener(new MyOnClickListener());

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //passing the clicked position to the parent class
            onItemClickListener.onItemClick(null, view, getAdapterPosition(), view.getId());
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, String> mMap = SubjectValues.get(position);
        holder.textView.setText(mMap.get("text"));
    }

    @Override
    public int getItemCount() {
        return SubjectValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View v) {
            super(v);

            textView = (TextView) v.findViewById(R.id.subject_textview);
        }
    }
}