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
     * Obtiene los partidos de la Copa del Mundo.
     */
    @GET("v4/competitions/WC/matches")
    suspend fun getWorldCupMatches(): Response<MatchesResponse>

    /**
     * Obtiene los partidos de La Liga (España).
     */
    @GET("v4/competitions/PD/matches")
    suspend fun getLaLigaMatches(): Response<MatchesResponse>

    /**
     * Obtiene los partidos de la Premier League (Inglaterra).
     */
    @GET("v4/competitions/PL/matches")
    suspend fun getPremierLeagueMatches(): Response<MatchesResponse>

    /**
     * Obtiene los partidos de la Serie A (Italia).
     */
    @GET("v4/competitions/SA/matches")
    suspend fun getSerieAMatches(): Response<MatchesResponse>

    /**
     * Obtiene los partidos de la Bundesliga (Alemania).
     */
    @GET("v4/competitions/BL1/matches")
    suspend fun getBundesligaMatches(): Response<MatchesResponse>

    /**
     * Obtiene los partidos de la Ligue 1 (Francia).
     */
    @GET("v4/competitions/FL1/matches")
    suspend fun getLigue1Matches(): Response<MatchesResponse>

    /**
     * Obtiene los partidos de la Primeira Liga (Portugal).
     */
    @GET("v4/competitions/PPL/matches")
    suspend fun getPrimeiraLigaMatches(): Response<MatchesResponse>

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
     */
    @GET("v4/teams")
    suspend fun getTeams(): Response<TeamsResponse>

    /**
     * Obtiene una lista de equipos para una competición específica.
     */
    @GET("v4/competitions/{competitionId}/teams")
    suspend fun getTeamsInCompetition(
        @Path("competitionId") competitionId: String
    ): Response<TeamsResponse>
}