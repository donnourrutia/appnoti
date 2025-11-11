package com.example.notigoal.data.remote

import com.example.notigoal.data.model.Match
import com.example.notigoal.data.model.MatchesResponse
import com.example.notigoal.data.model.TeamsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Esta es la "Interfaz" de la API.
 * Aquí definimos QUÉ llamadas podemos hacer.
 * El API Key ya no se pasa aquí, se gestiona con un Interceptor.
 */
interface FootballApi {

    /**
     * Obtiene todos los partidos.
     */
    @GET("v4/matches")
    suspend fun getMatches(): Response<MatchesResponse>

    /**
     * Obtiene los partidos de la Champions League.
     */
    @GET("v4/competitions/CL/matches")
    suspend fun getChampionsLeagueMatches(): Response<MatchesResponse>

    /**
     * Obtiene los detalles de un único partido por su ID.
     */
    @GET("v4/matches/{id}")
    suspend fun getMatchById(@Path("id") matchId: Int): Response<Match>

    /**
     * Endpoint para obtener los próximos partidos de un equipo específico.
     */
    @GET("v4/teams/{teamId}/matches")
    suspend fun getTeamMatches(
        @Path("teamId") teamId: Int,
        @Query("status") status: String = "SCHEDULED"
    ): Response<MatchesResponse>

    /**
     * Obtiene una lista de todos los equipos disponibles.
     * Nota: Con el plan gratuito, esto puede estar limitado a ciertas ligas.
     */
    @GET("v4/teams")
    suspend fun getTeams(): Response<TeamsResponse>

    /**
     * Obtiene una lista de equipos para una competición específica.
     * @param competitionId El código de la competición (ej. "PD" para La Liga, "PL" para Premier League).
     */
    @GET("v4/competitions/{competitionId}/teams")
    suspend fun getTeamsInCompetition(
        @Path("competitionId") competitionId: String
    ): Response<TeamsResponse>
}
