package com.example.notigoal.data.model

// Representa el marcador
data class Score(
    val fullTime: FullTimeScore?
)

// El JSON de football-data.org anida el marcador así: score -> fullTime -> home/away
data class FullTimeScore(
    val home: Int?, // Usamos Int? (nullable) por si el partido no ha empezado (valor null)
    val away: Int?
)
