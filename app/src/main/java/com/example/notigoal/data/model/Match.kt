package com.example.notigoal.data.model

import com.google.gson.annotations.SerializedName

// Representa un único partido de fútbol.
// Esta clase combina la información que ya tenías con la que faltaba.
data class Match(
    val id: Int,
    val competition: Competition,
    val utcDate: String,
    val status: String,
    val homeTeam: Team,
    val awayTeam: Team,        val score: Score,
    val venue: String? // <-- CAMPO AÑADIDO PARA EL ESTADIO
)


// Esta clase anidada representa el marcador.
// La hacemos nullable (?) porque un partido programado aún no tiene marcador.
data class Score(
    val fullTime: TimeScore?
)

// Representa el marcador de tiempo completo.
// Los goles también pueden ser nulos si el partido no ha comenzado.
data class TimeScore(
    val home: Int?, val away: Int?
)