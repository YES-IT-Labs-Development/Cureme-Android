package com.bussiness.curemegptapp.di

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager
) : Interceptor {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline()) {
            throw NoNetworkException()
        }
        val token = sessionManager.getToken()
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        //  Add Authorization header if token exists
        if (token.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        //  Add common headers (optional)
        requestBuilder.addHeader("Accept", "application/json")
        // NOTE: Do NOT set Content-Type globally — Retrofit sets it automatically
        // per request type (application/json for @Body, multipart/form-data for @Multipart).
        // Forcing Content-Type: application/json here breaks ALL multipart file uploads.
        val newRequest = requestBuilder.build()
        val networkType = getNetworkType()
        // 🔹 Network based header (optional but useful)
        when (networkType) {
            "WIFI" -> requestBuilder.addHeader("X-Network-Type", "WIFI")
            "MOBILE" -> requestBuilder.addHeader("X-Network-Type", "MOBILE")
        }

        return chain.proceed(newRequest)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Wi-Fi is considered fast
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Cellular: check if internet is validated and bandwidth is reasonable
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                        capabilities.linkDownstreamBandwidthKbps > 2000
            }

            else -> false
        }
    }

    private fun getNetworkType(): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return "NO_INTERNET"
        val caps = cm.getNetworkCapabilities(network) ?: return "NO_INTERNET"
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "MOBILE"
            else -> "OTHER"
        }
    }

}
