package br.com.memoriaplay.features.jogo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.jogo.model.NivelJogo
import kotlin.math.roundToInt

private val roxo = Color(0xFF673AB7)
private val rosa = Color(0xFFFF91C2)
private val fundo = Brush.verticalGradient(
    listOf(Color(0xFFFFF3D8), Color(0xFFFFE5F3), Color(0xFFE7E5FF))
)

@Composable
fun SelecionarModoScreen(
    aoSelecionarNivel: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var pares by rememberSaveable { mutableIntStateOf(6) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(fundo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = rosa
                )
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎮 Memória Play", color = roxo)
                    Text(
                        "Escolha o nível",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B297B)
                    )
                    Text(
                        "Cada desafio tem um tamanho diferente.",
                        color = roxo
                    )
                }
            }
        }

        item {
            OpcaoNivel(
                emoji = "🌱",
                titulo = "Fácil",
                descricao = "3 pares • 6 cartas",
                cor = Color(0xFFE6F7D4)
            ) {
                aoSelecionarNivel(NivelJogo.FACIL.name, 3)
            }
        }

        item {
            OpcaoNivel(
                emoji = "⭐",
                titulo = "Médio",
                descricao = "8 pares • 16 cartas",
                cor = Color(0xFFFFF0C8)
            ) {
                aoSelecionarNivel(NivelJogo.MEDIO.name, 8)
            }
        }

        item {
            OpcaoNivel(
                emoji = "🔥",
                titulo = "Difícil",
                descricao = "12 pares • 24 cartas",
                cor = Color(0xFFFFDFE8)
            ) {
                aoSelecionarNivel(NivelJogo.DIFICIL.name, 12)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "🎨 Personalizado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = roxo
                    )
                    Text("Escolha de 3 a 12 pares.")

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { pares-- },
                            enabled = pares > 3
                        ) {
                            Text("−", style = MaterialTheme.typography.titleLarge)
                        }
                        Text(
                            "$pares pares • ${pares * 2} cartas",
                            fontWeight = FontWeight.Bold,
                            color = roxo
                        )
                        IconButton(
                            onClick = { pares++ },
                            enabled = pares < 12
                        ) {
                            Text("+", style = MaterialTheme.typography.titleLarge)
                        }
                    }

                    Slider(
                        value = pares.toFloat(),
                        onValueChange = {
                            pares = it.roundToInt().coerceIn(3, 12)
                        },
                        valueRange = 3f..12f,
                        steps = 8
                    )

                    Button(
                        onClick = {
                            aoSelecionarNivel(
                                NivelJogo.PERSONALIZADO.name,
                                pares
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = roxo
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Jogar personalizado")
                    }
                }
            }
        }
    }
}

@Composable
private fun OpcaoNivel(
    emoji: String,
    titulo: String,
    descricao: String,
    cor: Color,
    aoJogar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = cor
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(emoji, modifier = Modifier.padding(12.dp))
            }

            Column(Modifier.weight(1f)) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = roxo
                )
                Text(descricao, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = aoJogar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = roxo
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Jogar")
            }
        }
    }
}
