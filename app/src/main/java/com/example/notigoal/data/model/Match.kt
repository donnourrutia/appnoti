package com.example.notigoal.data.model

import com.google.gson.annotations.SerializedName

// Representa un único partido de fútbol
data class Match(
    val id: Int,

    // Usamos @SerializedName porque el JSON usa "homeTeam"
    // pero en Kotlin queremos usar "homeTeam" (camelCase)
    @SerializedName("homeTeam")
    val homeTeam: Team,

    @SerializedName("awayTeam")
    val awayTeam: Team,

    val score: Score,
    val status: String // Ej: "IN_PLAY", "PAUSED", "FINISHED"
)
