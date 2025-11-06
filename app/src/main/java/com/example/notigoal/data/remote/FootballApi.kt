package com.example.notigoal.data.remote

import com.example.notigoal.data.model.MatchesResponse
import retrofit2.http.GET

/**
 * Esta es la "Interfaz" de la API.
 * Aquí definimos QUÉ llamadas podemos hacer.
 * Es como el menú de un restaurante.
 */
interface FootballApi {

    /**
     * Define un endpoint para obtener los partidos.
     * @GET("matches") se combinará con la URL Base (que definiremos en RetrofitInstance)
     * para formar: "https://api.football-data.org/v4/matches"
     *
     * Usamos 'suspend' porque es una operación de red y la llamaremos
     * desde una Coroutine (en nuestro ViewModel).
     *
     * Devuelve el objeto 'MatchesResponse' que creamos en el paso anterior.
     */
    @GET("matches")
    suspend fun getMatches(): MatchesResponse
}