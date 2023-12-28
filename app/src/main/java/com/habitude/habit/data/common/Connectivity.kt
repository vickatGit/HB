package com.habitude.habit.data.common

import android.content.Context
import android.net.ConnectivityManager

object Connectivity {

        fun isInternetConnected(context:Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

}