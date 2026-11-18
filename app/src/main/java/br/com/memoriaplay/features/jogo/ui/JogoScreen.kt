package br.com.memoriaplay.features.jogo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.jogo.viewmodel.JogoViewModel

private val roxo = Color(0xFF673AB7)
private val rosa = Color(0xFFFF91C2)
private val fundo = Brush.verticalGradient(
    listOf(Color(0xFFFFF3D8), Color(0xFFFFE5F3), Color(0xFFE7E5FF))
)

@Composable
fun JogoScreen(
    viewModel: JogoViewModel,
    aoVoltar: () -> Unit,
    aoConcluir: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    val configuracao = estado.configuracao

    LaunchedEffect(estado.finalizado) {
        if (estado.finalizado) aoConcluir()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(fundo).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = rosa),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text("🎮 Memória Play", color = roxo)
                Text(
                    "🧩 Partida em andamento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B297B)
                )
                Text(
                    "${configuracao?.nivel?.titulo ?: "Jogo"} " +
                        "• ${estado.nomeTema}",
                    color = roxo
                )
                configuracao?.desafio?.let { desafio ->
                    Text("🏆 Missão: ${desafio.titulo}", color = roxo,
                        fontWeight = FontWeight.Bold)
                    desafio.tempoLimiteSegundos?.takeIf { it > 0 }?.let {
                        Text("⏱️ Limite de tempo: $it segundos", color = roxo)
                    }
                    desafio.tentativasMaximas?.takeIf { it > 0 }?.let {
                        Text("🎯 Limite de tentativas: $it", color = roxo)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IndicadorJogo(
                "⏱️ Tempo",
                formatarTempo(estado.tempoSegundos),
                Modifier.weight(1f)
            )
            IndicadorJogo(
                "🎯 Tentativas",
                "${estado.tentativas}",
                Modifier.weight(1f)
            )
            IndicadorJogo(
                "⭐ Pares",
                "${estado.paresEncontrados}/" +
                    "${configuracao?.quantidadePares ?: 0}",
                Modifier.weight(1f)
            )
        }

        configuracao?.desafio?.let { desafio ->
            val tempoEsgotado = desafio.tempoLimiteSegundos?.takeIf { it > 0 }
                ?.let { estado.tempoSegundos > it } ?: false
            val tentativasEsgotadas = desafio.tentativasMaximas?.takeIf { it > 0 }
                ?.let { estado.tentativas > it } ?: false
            if (tempoEsgotado || tentativasEsgotadas) {
                Text(
                    "⚠️ Limite do desafio ultrapassado. Você ainda pode terminar " +
                        "a partida normalmente, mas esta missão não será concluída.",
                    color = Color(0xFF9F2929)
                )
            }
        }

        when {
            estado.carregando -> {
                CircularProgressIndicator(color = roxo)
                Text("Preparando as cartas...")
            }

            estado.mensagemErro != null -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(estado.mensagemErro.orEmpty())
                        Button(
                            onClick = {
                                configuracao?.let(viewModel::iniciarPartida)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = roxo
                            )
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            else -> {
                Text(
                    estado.mensagem,
                    color = roxo,
                    style = MaterialTheme.typography.bodyMedium
                )

                val colunas = if (estado.cartas.size <= 6) 3 else 4
                LazyVerticalGrid(
                    columns = GridCells.Fixed(colunas),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.cartas, key = { it.id }) { carta ->
                        CartaComponente(
                            carta = carta,
                            bloqueada = estado.bloqueado,
                            aoVirar = viewModel::selecionarCarta
                        )
                    }
                }
            }
        }

        TextButton(
            onClick = aoVoltar,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Sair da partida", color = roxo)
        }
    }
}

@Composable
private fun IndicadorJogo(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.labelSmall)
            Text(
                valor,
                fontWeight = FontWeight.Bold,
                color = roxo,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

fun formatarTempo(segundos: Int): String {
    val minutos = segundos / 60
    val resto = segundos % 60
    return "%02d:%02d".format(minutos, resto)
}
