package com.shoegazerwithak.lesswrongeveryday.ui

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.shoegazerwithak.lesswrongeveryday.R

class FragmentError : Fragment(), View.OnClickListener {
    private var mListener: OnRetryListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        val rootView = inflater.inflate(R.layout.error_screen_artists_list, container, false)
        val buttonRetry = rootView.findViewById(R.id.button_retry) as Button
        buttonRetry.setOnClickListener(this)
        return rootView
    }

    fun setOnRetryListener(listener: () -> Unit) {
        mListener = listener as OnRetryListener
    }

    override fun onClick(v: View) = mListener!!.onRetry()

    interface OnRetryListener {
        fun onRetry()
    }

    companion object {
        fun newInstance(): FragmentError = FragmentError()
    }
}