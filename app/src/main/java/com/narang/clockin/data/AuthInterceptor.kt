package com.narang.clockin.data

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class AuthInterceptor(
    private val tokenProvider: () -> String? = { null }
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider()
        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Timber.d("AuthInterceptor: added Bearer token")
        }
        return chain.proceed(requestBuilder.build())
    }
}
