package br.com.memoriaplay.features.jogo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.jogo.viewmodel.JogoViewModel
import br.com.memoriaplay.features.desafios.model.AvaliadorDesafio
import br.com.memoriaplay.R

private val roxo = Color(0xFF673AB7)
private val rosa = Color(0xFFFF91C2)
private val fundo = Brush.verticalGradient(
    listOf(Color(0xFFFFF3D8), Color(0xFFFFE5F3), Color(0xFFE7E5FF))
)

@Composable
fun ResultadoScreen(
    viewModel: JogoViewModel,
    aoJogarNovamente: () -> Unit,
    aoVerRanking: () -> Unit,
    aoVoltar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    val config = estado.configuracao
    val desafioCumprido = config?.desafio?.let {
        AvaliadorDesafio.avaliar(
            desafio = it,
            paresEncontrados = estado.paresEncontrados,
            tempoSegundos = estado.tempoSegundos,
            tentativas = estado.tentativas
        ).concluido
    }

    BackHandler { if (!estado.salvando) aoVoltar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fundo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = rosa)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ilustracao_vitoria),
                    contentDescription = null,
                    modifier = Modifier.height(120.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    if (desafioCumprido == false) "🏁 Partida concluída!"
                    else "🎉 Parabéns!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B297B)
                )
                Text(
                    "Você encontrou todos os pares!",
                    color = roxo,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (config?.desafio != null) {
            val avaliacao = AvaliadorDesafio.avaliar(
                desafio = config.desafio,
                paresEncontrados = estado.paresEncontrados,
                tempoSegundos = estado.tempoSegundos,
                tentativas = estado.tentativas
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🏆 ${config.desafio.titulo}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = roxo)
                    Text(
                        if (avaliacao.concluido) "✅ Desafio concluído!"
                        else "❌ Desafio não concluído",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (avaliacao.concluido) Color(0xFF216B3A)
                                else Color(0xFF9F2929)
                    )
                    Text(avaliacao.mensagem)
                    if (!avaliacao.concluido) {
                        Text("A vitória continua válida para o Ranking normal.")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "🏆 Resultado da partida",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = roxo
                )
                Text("Tema: ${estado.nomeTema}")
                Text("Modo: ${config?.nivel?.titulo ?: ""}")
                Text("Pares: ${config?.quantidadePares ?: 0}")
                Text("Tempo: ${formatarTempo(estado.tempoSegundos)}")
                Text("Tentativas: ${estado.tentativas}")

                when {
                    estado.salvando -> {
                        CircularProgressIndicator(color = roxo)
                        Text("Salvando seu resultado...")
                    }
                    estado.resultadoSalvo -> {
                        Text(
                            "✅ Resultado salvo no ranking!",
                            color = Color(0xFF216B3A)
                        )
                    }
                    estado.mensagemErro != null -> {
                        Text(
                            "Não foi possível salvar a partida.",
                            color = Color(0xFF9F2929)
                        )
                        Text(estado.mensagemErro.orEmpty())
                        OutlinedButton(
                            onClick = viewModel::tentarSalvarNovamente
                        ) {
                            Text("Tentar salvar novamente")
                        }
                    }
                }
            }
        }

        Button(
            onClick = aoJogarNovamente,
            enabled = !estado.salvando,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = roxo)
        ) {
            Text("🔁 Jogar novamente")
        }

        OutlinedButton(
            onClick = aoVerRanking,
            enabled = !estado.salvando,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("🏆 Ver ranking")
        }

        TextButton(
            onClick = aoVoltar,
            enabled = !estado.salvando
        ) {
            Text(
                if (config?.desafio != null) "Voltar aos desafios" else "Voltar ao início",
                color = roxo
            )
        }
    }
}
