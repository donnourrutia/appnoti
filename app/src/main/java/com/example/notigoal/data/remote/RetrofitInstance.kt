package com.example.notigoal.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // --- API KEY (ACTUALIZADA) ---
    private const val API_KEY = "37cdd5b0d4a94d20a4d444c047fafdd7"

    private const val BASE_URL = "https://api.football-data.org/"

    // Creamos un interceptor para añadir la API Key a todas las llamadas
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Auth-Token", API_KEY)
            .build()
        chain.proceed(request)
    }
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: FootballApi by lazy {
        retrofit.create(FootballApi::class.java)
    }
}