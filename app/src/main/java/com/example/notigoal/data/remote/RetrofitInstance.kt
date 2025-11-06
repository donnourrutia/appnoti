package com.example.notigoal.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Usamos un 'object' (Singleton) para asegurarnos de que solo exista
 * UNA instancia de Retrofit en toda la app.
 */
object RetrofitInstance {

    // --- ESTA ES TU LLAVE SECRETA ---
    // La sacamos de tu correo
    private const val API_KEY = "0fbe3a43da834080a6be071fc33521d6"
    // --- FIN DE LA LLAVE ---

    // La URL base de la API
    private const val BASE_URL = "https://api.football-data.org/v4/"

    // Creamos un "interceptor". Esto es una pieza de código que
    // "intercepta" CADA llamada que hacemos para añadirle algo.
    // En este caso, añade la cabecera "X-Auth-Token" con tu API Key.
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Auth-Token", API_KEY)
            .build()
        chain.proceed(request)
    }

    // (Opcional pero MUY RECOMENDADO para depurar)
    // Esto nos mostrará en el Logcat (la consola de Android Studio)
    // la petición exacta que hacemos y la respuesta JSON que recibimos.
    // Es vital para encontrar errores.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Creamos el cliente de OkHttp y le añadimos nuestros dos interceptores
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor) // Comenta esta línea en producción
        .build()

    // Creamos la instancia de Retrofit usando 'lazy'
    // 'lazy' significa que el objeto no se creará hasta
    // la primera vez que realmente lo necesitemos. Es más eficiente.
    val api: FootballApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 1. Le damos la URL base
            .client(client) // 2. Le damos nuestro cliente con la API Key
            .addConverterFactory(GsonConverterFactory.create()) // 3. Le decimos que use Gson para "traducir" JSON a nuestras clases Kotlin
            .build()
            .create(FootballApi::class.java) // 4. Creamos la implementación de nuestra Interfaz 'FootballApi'
    }
}