package com.shoegazerwithak.lesswrongeveryday.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shoegazerwithak.lesswrongeveryday.ArticleViewActivity;
import com.shoegazerwithak.lesswrongeveryday.FragmentArtist;
import com.shoegazerwithak.lesswrongeveryday.MainActivity;
import com.shoegazerwithak.lesswrongeveryday.R;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Map<String, String>> SubjectValues;
    private FragmentArtist.ArtistsFragmentInteractionListener mListener;
    private Context context;

    public RecyclerViewAdapter(Context context1, List<Map<String, String>> SubjectValues1, FragmentArtist.ArtistsFragmentInteractionListener listener) {
        SubjectValues = SubjectValues1;
        mListener = listener;
        context = context1;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items, parent, false);
        return new ViewHolder(view1);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            textView = (TextView) itemView.findViewById(R.id.subject_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onListItemClick(SubjectValues.get(getLayoutPosition()));
//            Intent intent = new Intent(context, ArticleViewActivity.class);
//            intent.putExtra("link", SubjectValues.get(getLayoutPosition()).get("link"));
//            intent.putExtra("text", SubjectValues.get(getLayoutPosition()).get("text"));
//            context.startActivity(intent);

//            Toast.makeText(context, SubjectValues.get(getLayoutPosition()).get("link"), Toast.LENGTH_LONG).show();
//            Log.e("Item Click Position", String.valueOf(getLayoutPosition()));
        }
    }
}