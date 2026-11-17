
package br.com.memoriaplay.features.ranking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.features.ranking.data.RankingRepository
import br.com.memoriaplay.features.ranking.model.CriterioOrdenacao
import br.com.memoriaplay.features.ranking.viewmodel.RankingViewModel
import kotlin.math.roundToInt

private val roxo = Color(0xFF673AB7)
private val rosa = Color(0xFFFF91C2)
private val azul = Color(0xFF86D4FF)
private val amarelo = Color(0xFFFFCC70)
private val fundo = Brush.verticalGradient(
    listOf(Color(0xFFFFF3D8), Color(0xFFFFE5F3), Color(0xFFE7E5FF))
)

@Composable
fun RankingScreen(viewModel: RankingViewModel? = null) {
    val contexto = LocalContext.current.applicationContext

    val fabrica = remember(contexto) {
        viewModelFactory {
            initializer {
                val banco = AppDatabase.obter(contexto)
                RankingViewModel(RankingRepository(banco.partidaDao()))
            }
        }
    }

    val vm: RankingViewModel = viewModel ?: composeViewModel(factory = fabrica)
    val estado by vm.uiState.collectAsState()
    var aba by rememberSaveable { mutableIntStateOf(0) }

    val partidas = if (aba == 0) estado.partidasRanking else estado.historico

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(fundo)
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = rosa),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        "🏆 Ranking de Partidas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B297B)
                    )
                    Text("⭐ Memória Play ⭐", color = roxo)
                    Text("Acompanhe suas melhores partidas!", color = roxo)
                }
            }
        }

        item {
            Bloco("🎯 Filtrar por dificuldade") {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        null to "Todos",
                        "Fácil" to "Fácil",
                        "Médio" to "Médio",
                        "Difícil" to "Difícil"
                    ).forEach { (modo, nome) ->
                        Opcao(nome, estado.filtro.modo == modo) {
                            vm.selecionarModo(modo)
                        }
                    }
                }
            }
        }

        item {
            val pares = estado.filtro.quantidadePares

            Bloco("🧩 Quantidade de pares: ${pares ?: "Todas"}") {
                Slider(
                    value = (pares ?: 3).toFloat(),
                    onValueChange = {
                        vm.selecionarQuantidadePares(
                            it.roundToInt().coerceIn(3, 12)
                        )
                    },
                    valueRange = 3f..12f,
                    steps = 8
                )
                TextButton(onClick = { vm.selecionarQuantidadePares(null) }) {
                    Text("Mostrar todas as quantidades")
                }
            }
        }

        item {
            Bloco("📊 Ordenar Ranking por") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Opcao(
                        "Tempo",
                        estado.filtro.ordenarPor == CriterioOrdenacao.TEMPO
                    ) {
                        vm.selecionarOrdenacao(CriterioOrdenacao.TEMPO)
                    }

                    Opcao(
                        "Tentativas",
                        estado.filtro.ordenarPor == CriterioOrdenacao.TENTATIVAS
                    ) {
                        vm.selecionarOrdenacao(CriterioOrdenacao.TENTATIVAS)
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = vm::limparFiltros,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = roxo
                )
            ) {
                Text("Limpar filtros")
            }
        }

        item {
            TabRow(
                selectedTabIndex = aba,
                containerColor = Color.White,
                contentColor = roxo
            ) {
                Tab(
                    selected = aba == 0,
                    onClick = { aba = 0 },
                    text = { Text("Ranking") }
                )
                Tab(
                    selected = aba == 1,
                    onClick = { aba = 1 },
                    text = { Text("Histórico") }
                )
            }
        }

        when {
            estado.carregando -> item {
                CircularProgressIndicator(color = roxo)
            }

            estado.mensagemErro != null -> item {
                Text(estado.mensagemErro ?: "", color = Color.Red)
            }

            partidas.isEmpty() -> item {
                Bloco("🏅 Resultados") {
                    Text(
                        if (aba == 0)
                            "Nenhuma vitória encontrada para os filtros selecionados."
                        else
                            "Nenhuma partida encontrada no histórico."
                    )
                }
            }

            else -> itemsIndexed(partidas) { indice, partida ->
                CartaoPartida(partida, indice + 1, aba == 0)
            }
        }
    }
}

@Composable
private fun Bloco(titulo: String, conteudo: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(titulo, fontWeight = FontWeight.Bold, color = roxo)
            conteudo()
        }
    }
}

@Composable
private fun Opcao(nome: String, selecionado: Boolean, clicar: () -> Unit) {
    FilterChip(
        selected = selecionado,
        onClick = clicar,
        label = { Text(nome) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = rosa,
            containerColor = Color.White
        )
    )
}

@Composable
private fun CartaoPartida(
    partida: PartidaEntity,
    posicao: Int,
    ranking: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ranking) amarelo else azul
        )
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                if (ranking) "$posicao º - ${partida.jogador}"
                else partida.jogador,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B297B),
                style = MaterialTheme.typography.titleMedium
            )
            Text("Modo: ${partida.modo}")
            Text("Pares: ${partida.quantidadePares}")
            Text("Tempo: ${partida.tempoSegundos / 60}m " +
                    "${(partida.tempoSegundos % 60).toString().padStart(2, '0')}s")
            Text("Tentativas: ${partida.tentativas}")

            if (!ranking) {
                Text(
                    if (partida.venceu) "Resultado: Vitória"
                    else "Resultado: Sem vitória",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
