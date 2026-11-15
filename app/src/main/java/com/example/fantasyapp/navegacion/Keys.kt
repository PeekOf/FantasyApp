package com.example.fantasyapp.navegacion

sealed class Rutas(val ruta: String){
    object Lista : Rutas("lista")
    object Crear : Rutas("crear")
    object Detalle : Rutas("detalle/{jugadorId}"){
        fun crearRuta(id: Int) = "detalle/$id"
    }
}