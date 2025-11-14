package com.example.notigoal.util.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.notigoal.NotiGoalApplication
import com.example.notigoal.data.model.Match
import com.example.notigoal.data.remote.FootballApi
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import com.example.notigoal.util.NotificationHelper
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import android.util.Log
import java.time.Instant

class GoalCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val favoriteTeamsRepository: FavoriteTeamsRepository
    private val footballApi: FootballApi

    init {
        val application = appContext as NotiGoalApplication
        favoriteTeamsRepository = application.container.favoriteTeamsRepository
        footballApi = application.container.footballApi
    }

    override suspend fun doWork(): Result {
        Log.d("GoalCheckWorker", "Iniciando verificación de goles...")
        return try {
            val favoriteTeams = favoriteTeamsRepository.getAllFavoriteTeams().firstOrNull() ?: emptyList()

            if (favoriteTeams.isEmpty()) {
                Log.d("GoalCheckWorker", "No hay equipos favoritos para monitorear.")
                return Result.success()
            }

            val relevantMatches = mutableListOf<Match>()


            for (favTeam in favoriteTeams) {
                try {

                    val response = footballApi.getTeamMatches(teamId = favTeam.id, status = "IN_PLAY")
                    if (response.isSuccessful) {
                        response.body()?.matches?.let { matches ->
                            relevantMatches.addAll(matches)
                        }
                    } else {
                        Log.e("GoalCheckWorker", "Error al obtener partidos para ${favTeam.name}: ${response.code()}")
                    }
                } catch (e: IOException) {
                    Log.e("GoalCheckWorker", "Error de red al obtener partidos para ${favTeam.name}: ${e.message}")
                } catch (e: Exception) {
                    Log.e("GoalCheckWorker", "Error inesperado al obtener partidos para ${favTeam.name}: ${e.message}")
                }
            }

            // Procesar los partidos relevantes
            if (relevantMatches.isNotEmpty()) {
                Log.d("GoalCheckWorker", "Partidos relevantes encontrados: ${relevantMatches.size}")
                relevantMatches.forEach { match ->

                    if (match.status == "IN_PLAY" || match.status == "PAUSED") {
                        val homeScore = match.score?.fullTime?.home ?: 0
                        val awayScore = match.score?.fullTime?.away ?: 0

                        val scoringTeamName: String?
                        val currentScore: String = "$homeScore - $awayScore"
                        val minute = if (match.utcDate != null) {
                            try {
                                val zonedDateTime = Instant.parse(match.utcDate).atZone(ZonedDateTime.now().zone)
                                val now = ZonedDateTime.now(zonedDateTime.zone)
                                val duration = java.time.Duration.between(zonedDateTime, now)
                                val minutes = duration.toMinutes().toInt()
                                if (minutes in 1..120) minutes.toString() + "'" else null
                            } catch (e: Exception) {
                                Log.e("GoalCheckWorker", "Error al parsear minuto: ${e.message}")
                                null
                            }
                        } else null

                        if (favoriteTeams.any { it.id == match.homeTeam.id } && homeScore > 0) {
                            scoringTeamName = match.homeTeam.name
                            NotificationHelper.showGoalNotification(
                                applicationContext,
                                scoringTeamName,
                                match.awayTeam.name,
                                currentScore,
                                minute,
                                match.id //
                            )
                            Log.d("GoalCheckWorker", "Notificación simulada para gol de ${scoringTeamName}")
                        } else if (favoriteTeams.any { it.id == match.awayTeam.id } && awayScore > 0) {
                            scoringTeamName = match.awayTeam.name
                            NotificationHelper.showGoalNotification(
                                applicationContext,
                                match.homeTeam.name,
                                scoringTeamName,
                                currentScore,
                                minute,
                                match.id //
                            )
                            Log.d("GoalCheckWorker", "Notificación simulada para gol de ${scoringTeamName}")
                        }
                    }
                }
            } else {
                Log.d("GoalCheckWorker", "No hay partidos en vivo de equipos favoritos para procesar.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("GoalCheckWorker", "Error inesperado en GoalCheckWorker: ${e.message}", e)
            Result.retry()
        }
    }
}