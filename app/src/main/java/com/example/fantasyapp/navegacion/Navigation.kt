package com.example.fantasyapp.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fantasyapp.modelo.Jugador
import com.example.fantasyapp.data.JugadoresData
import com.example.fantasyapp.pantallas.CrearJugadorScreen
import com.example.fantasyapp.pantallas.DetalleJugadorScreen
import com.example.fantasyapp.pantallas.ListaJugadoresScreen

    @Composable
    fun AppNavigation(){
        val navController = rememberNavController()

        val jugadores = remember{
            mutableStateListOf<Jugador>().apply{ addAll(JugadoresData.jugadoresIniciales) }
        }

        NavHost(navController = navController, startDestination = Rutas.Lista.ruta){
            composable(Rutas.Lista.ruta) {
                ListaJugadoresScreen(
                    jugadores = jugadores,
                    onAddClick = {navController.navigate(Rutas.Crear.ruta)},
                    onBorrarJugadores = { jugadoresABorrar -> jugadores.removeAll(jugadoresABorrar) },
                    onJugadorClick = { id ->
                        navController.navigate(route = Rutas.Detalle.crearRuta(id))
                    }
                )
            }

            composable(Rutas.Crear.ruta){
                CrearJugadorScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { nuevoJugador ->
                        jugadores.add(nuevoJugador)
                        navController.popBackStack()
                    }
                )
            }

            composable(Rutas.Detalle.ruta) { backStackEntry ->
                val idString = backStackEntry.arguments?.getString("jugadorId")
                val id = idString?.toIntOrNull()

                if (id != null) {
                    DetalleJugadorScreen(
                        jugadorId = id,
                        jugadores = jugadores,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

    }


