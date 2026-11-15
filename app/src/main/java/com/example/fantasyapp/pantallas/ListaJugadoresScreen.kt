package com.example.fantasyapp.pantallas


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fantasyapp.modelo.Jugador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.fantasyapp.R
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaJugadoresScreen(
    jugadores: List<Jugador>,
    onAddClick: () -> Unit,
    onBorrarJugadores: (List<Jugador>) -> Unit,
    onJugadorClick: (Int) -> Unit
){
    //Estados
    val estadoBarra = rememberSearchBarState()
    val estadoTexto = rememberTextFieldState()
    var textoBusqueda by remember { mutableStateOf("") }
    var busquedaActiva by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var modoBorrado by remember { mutableStateOf(false) }
    var seleccionados by remember { mutableStateOf(setOf<Jugador>()) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val equiposUnicos = jugadores.map {it.equipo}.distinct().sorted()
    val equiposFiltrados = equiposUnicos.filter{ it.contains(textoBusqueda, ignoreCase = true)}

    val jugadoresAMostrar = if(textoBusqueda.isBlank()){
        jugadores
    } else if(equiposUnicos.contains(textoBusqueda)) {
        jugadores.filter{it.equipo == textoBusqueda}
    } else {
        jugadores
    }

    if(mostrarDialogo){
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false},
            title = { Text("Borrar jugadores") },
            text = { Text("Estás seguro de que quieres borrar a los jugadores seleccionados?") },
            confirmButton = {
                TextButton (onClick = {
                    onBorrarJugadores(seleccionados.toList())
                    seleccionados = emptySet()
                    modoBorrado = false
                    mostrarDialogo = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false}) { Text("Cancelar") }
            }
        )
    }
    val campoBusqueda: @Composable () -> Unit = {
        SearchBarDefaults.InputField(
            textFieldState = estadoTexto,
            searchBarState = estadoBarra,
            onSearch = {
                textoBusqueda = estadoTexto.text.toString()
                scope.launch {
                    estadoBarra.animateToCollapsed()
                }
            },
            placeholder = { Text("Busca a jugadores por equipo") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.SportsBasketball,
                    contentDescription = "Buscar"
                )
            },
            trailingIcon = {
                if (estadoTexto.text.isNotEmpty()) {
                    IconButton(onClick = {
                        estadoTexto.clearText()
                        textoBusqueda = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            }
        )
    }


    Scaffold(
        modifier = Modifier.padding(top = 20.dp),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Bienvenido a tu fantasy league",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
                )

                // Barra de búsqueda colapsada
                SearchBar(
                    state = estadoBarra,
                    inputField = campoBusqueda,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                // Vista desplegada a pantalla completa al pulsar en la barra
                ExpandedFullScreenSearchBar(
                    state = estadoBarra,
                    inputField = campoBusqueda
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(equiposUnicos) { equipo ->
                            // Filtra los equipos que empiecen por lo que se escribe
                            if (equipo.startsWith(estadoTexto.text, ignoreCase = true)) {
                                ListItem(
                                    headlineContent = { Text(equipo) },
                                    leadingContent = {
                                        Icon(Icons.Default.Star, contentDescription = null)
                                    },
                                    trailingContent = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                    },
                                    modifier = Modifier.clickable {
                                        textoBusqueda = equipo
                                        estadoTexto.setTextAndPlaceCursorAtEnd(equipo)
                                        scope.launch {
                                            estadoBarra.animateToCollapsed()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 50.dp, top = 10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                // Si el modo borrado está activo, se desactiva/oculta el botón Add
                if (!modoBorrado) {
                    ExtendedFloatingActionButton(
                        onClick = onAddClick,
                        icon = { Icon(Icons.Default.Add, contentDescription = "Añadir") },
                        text = { Text("Add") },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                ExtendedFloatingActionButton(
                    onClick = {
                        if (modoBorrado) {
                            // Si ya estamos en modo borrado y hay seleccionados, mostramos el aviso
                            if (seleccionados.isNotEmpty()) mostrarDialogo = true
                            else modoBorrado = false // Si le da sin seleccionar nada, cancela el modo borrado
                        } else {
                            // Activar modo borrado
                            modoBorrado = true
                        }
                    },
                    icon = { Icon(Icons.Default.Delete, contentDescription = "Borrar") },
                    text = { Text("Delete") },
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jugadoresAMostrar, key = { it.id }) { jugador ->
                JugadorCard(
                    jugador = jugador,
                    modoBorrado = modoBorrado,
                    seleccionado = seleccionados.contains(jugador),
                    onSeleccionChange = { seleccionado ->
                        if (seleccionado) seleccionados = seleccionados + jugador
                        else seleccionados = seleccionados - jugador
                    },
                    onJugadorClick = onJugadorClick
                )
            }
        }
    }
}

@Composable
fun JugadorCard(
    jugador: Jugador,
    modoBorrado: Boolean,
    seleccionado: Boolean,
    onSeleccionChange: (Boolean) -> Unit,
    onJugadorClick: (Int) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaString = dateFormat.format(Date(jugador.fecha))

    // Asignación de color según el equipo
    val backgroundColor = when (jugador.equipo) {
        "Los Angeles Lakers" -> Color(0xFF552583) // Morado
        "Houston Rockets" -> Color(0xFFCE1141) // Rojo
        "Minnesota Timberwolves" -> Color(0xFF0C2340) // Azul oscuro
        "Cleveland Cavaliers" -> Color(0xFF860038) // Granate
        "Milwaukee Bucks" -> Color(0xFF00471B) // Verde oscuro
        "San Antonio Spurs" -> Color(0xFFC4CED4) // Gris plata
        "Oklahoma City Thunder" -> Color(0xFF007AC1) // Azul celeste
        "Miami Heat" -> Color(0xFF98002E) // Rojo carmesí
        "Dallas Mavericks" -> Color(0xFF00538C) // Azul royal
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                if (modoBorrado) {
                    onSeleccionChange(!seleccionado) // Si estamos borrando, marca el checkbox
                } else {
                    onJugadorClick(jugador.id) // Si NO estamos borrando, ve a Detalles
                }
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo placeholder temporal para la imagen del jugador
            Image(
                painter = painterResource(id = obtenerImagenJugador(jugador.nombre)),
                contentDescription = "Foto de ${jugador.nombre}",
                contentScale = ContentScale.Crop, // Recorta la foto para llenar el círculo sin deformarla
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape) // Hace que la imagen sea redonda
                    .background(Color.White.copy(alpha = 0.25f))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jugador.nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "VS ${jugador.equipoRival}, $fechaString",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = "${jugador.puntos} PP",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            // Checkbox que solo aparece si modoBorrado es true
            if (modoBorrado) {
                Checkbox(
                    checked = seleccionado,
                    onCheckedChange = onSeleccionChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        checkmarkColor = backgroundColor,
                        uncheckedColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}

fun obtenerImagenJugador(nombre: String): Int {
    return when (nombre.trim().lowercase()) {
        "lebron james" -> R.drawable.lebronjames
        "alperen sengun" -> R.drawable.alperen_sengun
        "donovan mitchell" -> R.drawable.donovan_mitchell
        "naz reid" -> R.drawable.naz_reid
        "damian lillard" -> R.drawable.damian_lillard
        "keldon johnson" -> R.drawable.keldon_johnson
        "chet holmgren" -> R.drawable.chet_holmgren
        "luka doncic" -> R.drawable.luca_doncic
        "jeremy sochan" -> R.drawable.jeremy_sochan
        "jimmy butler" -> R.drawable.jimmy_butler
        else -> R.drawable.ic_launcher_background // Imagen por defecto si no coincide ningún nombre
    }
}
