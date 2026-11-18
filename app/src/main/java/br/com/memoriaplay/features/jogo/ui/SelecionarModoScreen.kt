package br.com.memoriaplay.features.jogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelecionarModoScreen(
    aoSelecionarNivel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Escolha o nível",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Selecione a dificuldade para começar uma partida.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { aoSelecionarNivel("FACIL") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fácil", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { aoSelecionarNivel("MEDIO") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Médio", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { aoSelecionarNivel("DIFICIL") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Difícil", fontSize = 18.sp)
        }
    }
}
