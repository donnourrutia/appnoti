package com.example.notigoal.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // --- ¡LA API KEY AHORA VIVE AQUÍ! ---
    // Está centralizada y segura.
    private const val API_KEY = "0fbe3a43da834080a6be071fc33521d6"

    // --- URL Base de la API ---
    private const val BASE_URL = "https://api.football-data.org/"

    // Creamos un interceptor para añadir la API Key a todas las llamadas
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Auth-Token", API_KEY)
            .build()
        chain.proceed(request)
    }

    // Creamos un interceptor para ver los logs de las llamadas (muy útil para depurar)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Creamos el cliente de OkHttp y le añadimos los interceptors
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    // Creamos la instancia de Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Usamos el cliente personalizado
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Creamos una instancia de nuestra interfaz de API
    val api: FootballApi by lazy {
        retrofit.create(FootballApi::class.java)
    }
}
