
package br.com.memoriaplay.features.desafios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.desafios.viewmodel.DesafiosViewModel
import br.com.memoriaplay.features.desafios.model.Desafio

private val roxo = Color(0xFF673AB7)
private val rosa = Color(0xFFFF91C2)
private val amarelo = Color(0xFFFFCC70)
private val azul = Color(0xFF86D4FF)

private val fundo = Brush.verticalGradient(
    listOf(
        Color(0xFFFFF3D8),
        Color(0xFFFFE5F3),
        Color(0xFFE7E5FF)
    )
)

@Composable
fun DesafiosScreen(
    viewModel: DesafiosViewModel,
    aoIniciarDesafio: ((Desafio) -> Unit)? = null
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.carregarDesafios()
    }

    DesafiosScreenContent(
        estado = estado,
        aoTentarNovamente = viewModel::tentarNovamente,
        aoIniciarDesafio = aoIniciarDesafio
    )
}

@Composable
private fun DesafiosScreenContent(
    estado: DesafiosUiState,
    aoTentarNovamente: () -> Unit,
    aoIniciarDesafio: ((Desafio) -> Unit)?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(fundo)
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = rosa
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎮 Memória Play",
                        color = roxo,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Desafios",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B297B)
                    )

                    Text(
                        text = "🌟 Explore novos desafios e divirta-se!",
                        color = roxo
                    )
                }
            }
        }

        when {
            estado.carregando -> item {
                CardDesafio {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = roxo)
                        Text("Carregando desafios...")
                    }
                }
            }

            estado.mensagemErro != null -> item {
                CardDesafio {
                    Text(
                        text = "⚠️ Não foi possível acessar os desafios.",
                        fontWeight = FontWeight.Bold,
                        color = roxo
                    )

                    Text(
                        text = estado.mensagemErro,
                        color = Color(0xFFB3261E)
                    )

                    Button(
                        onClick = aoTentarNovamente,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = roxo
                        )
                    ) {
                        Text("Tentar novamente")
                    }
                }
            }

            estado.desafios.isEmpty() -> item {
                CardDesafio {
                    Text("🧩", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        text = "Nenhum desafio disponível.",
                        color = roxo,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            else -> items(estado.desafios) { desafio ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(text = "🏆")

                        Text(
                            text = desafio.titulo,
                            color = roxo,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )


                        Text(
                            text = desafio.descricao,
                            color = Color(0xFF655473)
                        )

                        Surface(
                            color = amarelo,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Pares: ${desafio.quantidadePares}",
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                ),
                                color = Color(0xFF4B297B),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        desafio.tempoLimiteSegundos?.takeIf { it > 0 }?.let { limite ->
                            Text("⏱️ Concluir em até $limite segundos", color = roxo)
                        }
                        desafio.tentativasMaximas?.takeIf { it > 0 }?.let { limite ->
                            Text("🎯 No máximo $limite tentativas", color = roxo)
                        }
                        if (aoIniciarDesafio != null) {
                            val valido = desafio.quantidadePares in 3..12 &&
                                desafio.id.isNotBlank()
                            Button(
                                onClick = { aoIniciarDesafio(desafio) },
                                enabled = valido,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = roxo)
                            ) {
                                Text("Iniciar desafio")
                            }
                            if (!valido) {
                                Text(
                                    "Este desafio não pode ser iniciado: " +
                                        "o jogo aceita de 3 a 12 pares.",
                                    color = Color(0xFFB3261E)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardDesafio(
    conteudo: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = conteudo
        )
    }
}
