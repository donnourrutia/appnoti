package com.example.notigoal.data.model

// La respuesta de la API no es solo una lista,
// usualmente es un objeto que CONTIENE la lista de partidos.
data class MatchesResponse(
    // La anotación @SerializedName es de la librería Gson.
    // Le dice a GSON: "Cuando veas 'matches' en el JSON,
    // guárdalo en esta variable llamada 'matchesList'".
    val matches: List<Match>
)