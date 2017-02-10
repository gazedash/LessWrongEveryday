package com.shoegazerwithak.lesswrongeveryday.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shoegazerwithak.lesswrongeveryday.R;

public class FragmentError extends Fragment implements View.OnClickListener {
    private OnRetryListener mListener;

    public FragmentError() {
    }

    public static FragmentError newInstance() {
        return new FragmentError();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.error_screen_artists_list, container, false);
        Button buttonRetry = (Button) rootView.findViewById(R.id.button_retry);
        buttonRetry.setOnClickListener(this);
        return rootView;
    }

    public void setOnRetryListener(OnRetryListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        mListener.onRetry();
    }

    public interface OnRetryListener {
        void onRetry();
    }
}
