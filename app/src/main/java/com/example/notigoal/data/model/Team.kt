package com.example.notigoal.data.model

// Representa a un equipo de fútbol
data class Team(
    val id: Int,
    val name: String,
    val shortName: String, // <-- EL CAMPO QUE FALTABA
    val crest: String // URL del escudo del equipo
)
