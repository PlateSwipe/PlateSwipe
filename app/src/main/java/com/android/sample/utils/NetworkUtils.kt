package com.android.sample.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Utility class for network-related operations. */
open class NetworkUtils {

  /**
   * Checks if the network is available.
   *
   * @param context The context used to get the ConnectivityManager.
   * @return True if the network is available, false otherwise.
   */
  fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
  }
}
