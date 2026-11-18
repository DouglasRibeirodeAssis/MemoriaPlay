package br.com.memoriaplay.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.home.viewmodel.HomeViewModel
import br.com.memoriaplay.ui.components.ErroComTentarNovamente
import br.com.memoriaplay.ui.components.EstadoVazio

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    aoIniciarPartida: () -> Unit,
    aoAbrirCatalogo: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Memória Play", style = MaterialTheme.typography.headlineLarge)
        Text("Treine sua memória no seu ritmo.", style = MaterialTheme.typography.bodyLarge)

        when {
            state.carregando -> CircularProgressIndicator()
            state.mensagemErro != null -> ErroComTentarNovamente(
                mensagem = state.mensagemErro!!,
                aoTentarNovamente = viewModel::carregarResumo
            )
            !state.resumo.possuiPartidas -> EstadoVazio(
                titulo = "Sua jornada começa aqui",
                mensagem = "Você ainda não concluiu nenhuma partida. Que tal jogar agora?"
            )
            else -> Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Seu resumo", style = MaterialTheme.typography.titleLarge)
                    Text("Partidas: ${state.resumo.totalPartidas}")
                    Text("Vitórias: ${state.resumo.totalVitorias}")
                    Text("Melhor tempo: ${state.resumo.melhorTempoSegundos?.let { "${it}s" } ?: "—"}")
                    Text("Último modo: ${state.resumo.ultimaPartidaModo ?: "—"}")
                }
            }
        }

        Button(onClick = aoIniciarPartida, modifier = Modifier.fillMaxWidth()) {
            Text("Iniciar nova partida")
        }
        Button(onClick = aoAbrirCatalogo, modifier = Modifier.fillMaxWidth()) {
            Text("Explorar catálogo")
        }
    }
}
