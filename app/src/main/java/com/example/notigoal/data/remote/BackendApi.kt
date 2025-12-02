package com.example.notigoal.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 1. MODELO DE DATOS
data class Feedback(
    val id: Long? = null,
    val userName: String,
    val message: String
)

// 2. INTERFAZ RETROFIT
// Define los métodos que usará la app para el CRUD (Create, Read)
interface BackendApi {

    // READ: Obtener todos los comentarios
    @GET("api/feedbacks")
    suspend fun getFeedbacks(): Response<List<Feedback>>

    // CREATE: Enviar un nuevo comentario
    @POST("api/feedbacks")
    suspend fun sendFeedback(@Body feedback: Feedback): Response<Feedback>
}

// 3. INSTANCIA RETROFIT
object BackendRetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: BackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)
    }
}