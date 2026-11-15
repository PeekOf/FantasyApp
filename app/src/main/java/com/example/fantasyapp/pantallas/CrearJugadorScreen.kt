package com.example.fantasyapp.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fantasyapp.modelo.Jugador

import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearJugadorScreen(
    onBack: () -> Unit,
    onSave: (Jugador) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var equipo by remember { mutableStateOf("") }
    var equipoRival by remember { mutableStateOf("") }

    var puntos by remember { mutableStateOf(0f) }

    var posicionesSeleccionadas by remember { mutableStateOf(setOf<String>()) }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var fechaSeleccionada by remember { mutableStateOf(System.currentTimeMillis()) }

    val nombresDisponibles = listOf(
        "LeBron James",
        "Alperen Sengun",
        "Donovan Mitchell",
        "Naz Reid",
        "Damian Lillard",
        "Keldon Johnson",
        "Chet Holmgren",
        "Luka Doncic",
        "Jeremy Sochan",
        "Jimmy Butler"
    )
    val equiposDisponibles = listOf(
        "Los Angeles Lakers",
        "Houston Rockets",
        "Cleveland Cavaliers",
        "Minnesota Timberwolves",
        "Milwaukee Bucks",
        "San Antonio Spurs",
        "Oklahoma City Thunder",
        "Dallas Mavericks",
        "Miami Heat"
    )
    val posicionesDisponibles = listOf("PG", "SG", "SF", "PF", "C")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Jugador") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- Nombre (Menú desplegable) ---
            Text("Introduce el nombre del jugador:")
            MenuDesplegable(
                opciones = nombresDisponibles,
                seleccion = nombre,
                onSeleccionChange = { nombre = it },
                label = "Selecciona un valor"
            )

            // --- Equipo (Menú desplegable) ---
            Text("El equipo en el que juega:")
            MenuDesplegable(
                opciones = equiposDisponibles,
                seleccion = equipo,
                onSeleccionChange = { equipo = it },
                label = "Selecciona un valor"
            )

            // --- Posiciones (RadioGroup falso, permite múltiples) ---
            Text("Selecciona una o más posiciones:")
            Column {
                posicionesDisponibles.forEach { posicion ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = posicionesSeleccionadas.contains(posicion),
                            onClick = {
                                if (posicionesSeleccionadas.contains(posicion)) {
                                    posicionesSeleccionadas = posicionesSeleccionadas - posicion
                                } else {
                                    posicionesSeleccionadas = posicionesSeleccionadas + posicion
                                }
                            }
                        )
                        Text(text = posicion, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            // --- Puntos (Slider) ---
            Text("El número de puntos que metió en el partido:")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = puntos,
                    onValueChange = { puntos = it },
                    valueRange = 0f..100f,
                    steps = 99,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = puntos.toInt().toString(),
                    modifier = Modifier.padding(start = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // --- Equipo Rival (Menú desplegable) ---
            Text("El equipo contra el que se jugó:")
            MenuDesplegable(
                opciones = equiposDisponibles,
                seleccion = equipoRival,
                onSeleccionChange = { equipoRival = it },
                label = "Selecciona un valor"
            )

            // --- Fecha (Selector / DatePicker) ---
            Text("Selecciona la fecha del partido:")
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            OutlinedButton(
                onClick = { mostrarDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(dateFormat.format(Date(fechaSeleccionada)))
            }

            if (mostrarDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fechaSeleccionada)
                DatePickerDialog(
                    onDismissRequest = { mostrarDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { fechaSeleccionada = it }
                            mostrarDatePicker = false
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // --- Botón Guardar ---
            Button(
                onClick = {
                    if (nombre.isNotBlank() && equipo.isNotBlank() && equipoRival.isNotBlank() && posicionesSeleccionadas.isNotEmpty()) {
                        val nuevoJugador = Jugador(
                            id = (0..1000000).random(),
                            nombre = nombre,
                            equipo = equipo,
                            posiciones = posicionesSeleccionadas.toList(),
                            puntos = puntos.toInt(),
                            equipoRival = equipoRival,
                            fecha = fechaSeleccionada
                        )
                        onSave(nuevoJugador)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Guardar Jugador")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDesplegable(
    opciones: List<String>,
    seleccion: String,
    onSeleccionChange: (String) -> Unit,
    label: String
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionChange(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}