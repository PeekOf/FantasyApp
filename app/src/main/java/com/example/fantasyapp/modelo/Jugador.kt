package com.example.fantasyapp.modelo

data class Jugador (
    val id: Int,
    val nombre: String,
    val equipo: String,
    val posiciones: List<String>,
    val puntos: Int,
    val equipoRival: String,
    val fecha: Long
)