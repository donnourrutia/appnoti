package com.example.notigoal.data.model
import com.google.gson.annotations.SerializedName
data class Match(
    val id: Int,
    val competition: Competition,
    val utcDate: String,
    val status: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val score: Score?,
    val venue: String?
)
data class Score(
    val fullTime: TimeScore?
)
data class TimeScore(
    val home: Int?, val away: Int?
)