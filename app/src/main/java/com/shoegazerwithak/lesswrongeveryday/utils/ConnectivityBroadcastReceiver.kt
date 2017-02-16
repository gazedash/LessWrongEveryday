package com.shoegazerwithak.lesswrongeveryday.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Checks if there's internet connection and sends the result outside the receiver
 */
class ConnectivityBroadcastReceiver : BroadcastReceiver() {

    private var mListener: ConnectivityListener? = null

    var isConnectionAvailable: Boolean = false
        private set
    fun setConnectivityListener(listener: (Boolean) -> Unit) {
        mListener = listener as ConnectivityListener
    }

    override fun onReceive(context: Context, intent: Intent) = checkConnection(context)

    private fun checkConnection(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val available = cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
        isConnectionAvailable = available
        mListener!!.onConnectionChecked(available)
    }

    interface ConnectivityListener {
        fun onConnectionChecked(available: Boolean)
    }
}