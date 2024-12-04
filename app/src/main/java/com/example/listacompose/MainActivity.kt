package com.example.listacompose



import android.app.Activity
import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navegacion()
        }
    }
}

@Composable
fun Navegacion() {
    val navegarControlador = rememberNavController()

    NavHost(navController = navegarControlador, startDestination = "Pantalla1") {
        composable("Pantalla1") { Pantalla1(navegarControlador) }
        composable("Pantalla2") { Pantalla2(navegarControlador) }
        composable("Pantalla3") { Pantalla3(navegarControlador) }
        composable(
            route = "Pantalla4/{puntosJugador1}/{puntosJugador2}",
            arguments = listOf(
                navArgument("puntosJugador1") { type = NavType.IntType },
                navArgument("puntosJugador2") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val puntosJugador1 = backStackEntry.arguments?.getInt("puntosJugador1") ?: 0
            val puntosJugador2 = backStackEntry.arguments?.getInt("puntosJugador2") ?: 0
            Pantalla4(navegarControlador, puntosJugador1, puntosJugador2)
        }
    }
}

@Composable
fun Pantalla1(navegarControlador: NavHostController) {
    var textState by remember { mutableStateOf("") }
    var names by remember { mutableStateOf(listOf<String>()) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Input Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = textState,
                    onValueChange = {
                        textState = it

                        errorMessage = "" // Clear error on text change
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Ingrese un nombre") },
                    isError = errorMessage.isNotBlank()
                )
                Button(
                    onClick = {
                        if (textState.isNotBlank() && !names.contains(textState)) {
                            names = listOf(textState) + names // Add to the start
                            textState = "" // Clear text field
                        } else if (names.contains(textState)) {
                            errorMessage = "El nombre ya está en la lista"
                        } else {
                            errorMessage = "El campo no puede estar vacío"
                        }
                    }
                ) {
                    Text("Agregar")
                }
            }

            // Display error message (if any)
            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de nombres
            Column(modifier = Modifier.weight(1f)) {
                names.forEachIndexed { index, name ->
                    NameItem(
                        name = name,
                        onDelete = {
                            names = names.toMutableList().apply { removeAt(index) }
                        }

                    )
                }
            }

            Row(

                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Counter at the bottom
                Text(
                    text = "Total nombres: ${names.size}",
                    style = MaterialTheme.typography.bodyLarge,
                   /* modifier = Modifier
                        .align(BottomCenter)
                        .padding(bottom = 16.dp)*/
                )
                //Espacio entre los dos
                Spacer(modifier = Modifier.width(100.dp))
                //Boton navegar
                Button(onClick = { navegarControlador.navigate("Pantalla2") }) { Text("Pantalla 2") }

            }


        }


    }
}


@Composable
fun NameItem(name: String, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onDelete) {
            Text("Borrar")
        }
    }
}


@Composable
fun Pantalla2(navegarControlador: NavHostController) {
    // Estados para los números y el resultado
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calculadora", style = MaterialTheme.typography.labelLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para el primer número
        TextField(
            value = number1,
            onValueChange = { number1 = it },
            label = { Text("Número 1") },
            placeholder = { Text("Ingrese un número") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para el segundo número
        TextField(
            value = number2,
            onValueChange = { number2 = it },
            label = { Text("Número 2") },
            placeholder = { Text("Ingrese otro número") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de operación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { result = calculate(number1, number2, "+") }) {
                Text("+")
            }
            Button(onClick = { result = calculate(number1, number2, "-") }) {
                Text("-")
            }
            Button(onClick = { result = calculate(number1, number2, "*") }) {
                Text("*")
            }
            Button(onClick = { result = calculate(number1, number2, "/") }) {
                Text("/")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el resultado
        Text(
            text = result?.let { "Resultado: $it" } ?: "Ingrese números y seleccione una operación",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {

            // Botón para regresar a la pantalla 1
            Button(onClick = { navegarControlador.navigate("Pantalla1") }) {
                Text("Pantalla 1")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {navegarControlador.navigate("Pantalla3")}) {
                Text("Pantalla 3")
            }

        }


    }
}

// Función para calcular el resultado basado en la operación
fun calculate(num1: String, num2: String, operator: String): String {
    val n1 = num1.toDoubleOrNull()
    val n2 = num2.toDoubleOrNull()

    if (n1 == null || n2 == null) return "Por favor ingrese números válidos"

    return when (operator) {
        "+" -> (n1 + n2).toString()
        "-" -> (n1 - n2).toString()
        "*" -> (n1 * n2).toString()
        "/" -> if (n2 != 0.0) (n1 / n2).toString() else "Error: División por cero"
        else -> "Operación no válida"
    }
}

@Composable
fun Pantalla3(navegarControlador: NavHostController){

    //Estados de los puntos de los jugadores
    var puntosJugador1 by remember { mutableStateOf<Int?>(null) }
    var puntosJugador2 by remember { mutableStateOf<Int?>(null) }

    // var botonPulsado by remember { mutableStateOf(false) }
    //Habilitar boton navegacion
    val botonPulsado = puntosJugador1 != null && puntosJugador2 != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pulsa un botón para obtener los puntos")
        Spacer(modifier = Modifier.height(15.dp))
        Button(onClick = {/*botonPulsado=true*/ puntosJugador1 = Random.nextInt(1,41)}) { Text("Jugador 1 -> ${puntosJugador1 ?: "Sin puntos"}")}
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {/*botonPulsado = true*/ puntosJugador2 = Random.nextInt(1,41)}) { Text("Jugador 2 -> ${puntosJugador2 ?: "Sin puntos"}")}
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {navegarControlador.navigate("Pantalla2")}) { Text("Pantalla 2")}
            Spacer(modifier = Modifier.width(10.dp))
            //Se lleva los puntos a la pantalla4
            Button(
                onClick = {
                    navegarControlador.navigate("Pantalla4/${puntosJugador1 ?: 0}/${puntosJugador2 ?: 0}")
                },
                enabled = botonPulsado
            ) {
                Text("Pantalla 4")
            }
        }



    }
}

@Composable
fun Pantalla4(navegarControlador: NavHostController, puntosJugador1: Int, puntosJugador2: Int){

    // Calcular el jugador con más puntos
    val (ganador, puntosGanador) = when {
        puntosJugador1 > puntosJugador2 -> "Jugador 1" to puntosJugador1
        puntosJugador2 > puntosJugador1 -> "Jugador 2" to puntosJugador2
        else -> "Empate" to puntosJugador1 // Ambos tienen la misma puntuación
    }

    //Para cerrar la aplicación
    val cerrar = (LocalContext.current as? Activity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //AQUI SE MUESTRA EL GANADOR
        Text(
            text = if (ganador == "Empate") {
                "¡Es un empate! Ambos jugadores tienen $puntosGanador puntos."
            } else {
                "El ganador es $ganador con $puntosGanador puntos."
            },
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {navegarControlador.navigate("Pantalla1")}) { Text("Pantalla 1")}
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {navegarControlador.navigate("Pantalla3")}) { Text("Pantalla 3")}
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {cerrar?.finishAffinity()}) { Text("Salir")}
        }
    }
}
