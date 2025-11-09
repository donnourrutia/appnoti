package com.example.notigoal.data.remote

// --- IMPORTS CORREGIDOS ---
import com.example.notigoal.data.model.Match
import com.example.notigoal.data.model.MatchesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query // <-- ¡IMPORT NUEVO Y NECESARIO!

/**
 * Esta es la "Interfaz" de la API.
 * Aquí definimos QUÉ llamadas podemos hacer.
 */
interface FootballApi {

    /**
     * Obtiene todos los partidos.
     */
    @GET("v4/matches")
    suspend fun getMatches(
        @Header("X-Auth-Token") authToken: String
    ): Response<MatchesResponse>

    /**
     * Obtiene los partidos de la Champions League.
     */
    @GET("v4/competitions/CL/matches")
    suspend fun getChampionsLeagueMatches(
        @Header("X-Auth-Token") authToken: String
    ): Response<MatchesResponse>

    /**
     * Obtiene los detalles de un único partido por su ID.
     */
    @GET("v4/matches/{id}")
    suspend fun getMatchById(
        @Header("X-Auth-Token") authToken: String,
        @Path("id") matchId: Int
    ): Response<Match>

    // ================== ¡NUEVA FUNCIÓN AÑADIDA! ==================
    /**
     * Endpoint para obtener los próximos partidos de un equipo específico.
     * GET https://api.football-data.org/v4/teams/{teamId}/matches?status=SCHEDULED
     */
    @GET("v4/teams/{teamId}/matches")
    suspend fun getTeamMatches(
        @Header("X-Auth-Token") authToken: String,
        @Path("teamId") teamId: Int,
        @Query("status") status: String = "SCHEDULED" // Pedimos solo los programados
    ): Response<MatchesResponse>
}
