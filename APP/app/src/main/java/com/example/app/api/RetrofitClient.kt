package com.example.app.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Единая точка настройки HTTP-клиента: адрес Docker API, API-ключ, таймауты и JSON-конвертер. */
object RetrofitClient {
    // `reverseApiPort` maps this device-local port to Docker on the host.
    private const val BASE_URL = "http://127.0.0.1:8080/"  // с впн
    //private const val BASE_URL = "http://10.0.2.2:8080/" // без впн
    private const val API_KEY = "helpdesk-api-key"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("X-API-Key", API_KEY)
                    .build()
            )
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
