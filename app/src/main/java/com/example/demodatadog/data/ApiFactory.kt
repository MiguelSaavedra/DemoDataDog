package com.example.demodatadog.data

import com.datadog.android.okhttp.DatadogInterceptor
import com.example.demodatadog.monitoring.DatadogTracker
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val pokeApi: PokeApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(DatadogInterceptor.Builder(DatadogTracker.firstPartyHosts()).build())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }
}
