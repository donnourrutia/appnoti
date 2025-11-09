package com.example.notigoal.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Usamos un 'object' para crear un Singleton.
 * Esto asegura que solo tengamos UNA instancia de Retrofit en toda la app,
 * lo cual es más eficiente.
 */
object RetrofitInstance {

    // La URL base de la API. Todas las llamadas partirán de aquí.
    private const val BASE_URL = "https://api.football-data.org/"

    // Creamos la instancia de Retrofit.
    // Usamos 'lazy' para que se cree solo la primera vez que la necesitemos.
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Le decimos que use Gson para convertir JSON
            .build()
    }

    // Creamos una propiedad pública que expone la API ya implementada por Retrofit.
    // Este es el objeto que usaremos desde nuestro ViewModel para hacer las llamadas.
    val api: FootballApi by lazy {
        retrofit.create(FootballApi::class.java)
    }
}
