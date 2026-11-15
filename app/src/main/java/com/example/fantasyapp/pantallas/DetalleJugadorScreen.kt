package com.example.fantasyapp.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fantasyapp.modelo.Jugador
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleJugadorScreen(
    jugadorId: Int,
    jugadores: List<Jugador>,
    onBack: () -> Unit
) {
    val jugadorSeleccionado = jugadores.find { it.id == jugadorId }

    if (jugadorSeleccionado == null) {
        // Pantalla de error por si el ID no existe
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Jugador no encontrado")
        }
        return
    }

    val todosLosPartidosDelJugador = jugadores.filter { it.nombre == jugadorSeleccionado.nombre }
    val totalPartidos = todosLosPartidosDelJugador.size
    val mediaPuntos = if (totalPartidos > 0) {
        todosLosPartidosDelJugador.map { it.puntos }.average()
    } else {
        0.0
    }
    val mediaRedondeada = (mediaPuntos * 100.0).roundToInt() / 100.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del jugador") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Título superior
            Text(
                text = "${jugadorSeleccionado.nombre} que juega en ${jugadorSeleccionado.equipo}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = obtenerImagenJugador(jugadorSeleccionado.nombre)),
                    contentDescription = "Foto de ${jugadorSeleccionado.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }

            Text(
                text = "Estadísticas: un total de $totalPartidos partido(s) jugados, " +
                        "media de puntos $mediaRedondeada, pero en el partido contra " +
                        "${jugadorSeleccionado.equipoRival} metió un total de ${jugadorSeleccionado.puntos}.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp),
                lineHeight = 24.sp
            )

            Text(
                text = "${jugadorSeleccionado.nombre} is an American professional basketball player " +
                        "for the ${jugadorSeleccionado.equipo} of the National Basketball Association (NBA). " +
                        "He is widely regarded as one of the greatest players in the history of the sport " +
                        "and is often compared to Michael Jordan in debates over the greatest basketball " +
                        "player of all time. He is the all-time leading scorer in NBA history and ranks " +
                        "fourth in career assists. He has won four NBA championships, four MVP awards, " +
                        "four Finals MVP awards, and three All-Star MVP awards.",
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                lineHeight = 20.sp,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}