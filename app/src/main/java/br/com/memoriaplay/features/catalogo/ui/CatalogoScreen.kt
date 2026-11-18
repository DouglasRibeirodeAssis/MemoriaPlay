
package br.com.memoriaplay.features.catalogo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.catalogo.model.TemaCatalogo
import br.com.memoriaplay.features.catalogo.viewmodel.CatalogoViewModel

private val roxo = Color(0xFF673AB7)
private val rosa = Color(0xFFFF91C2)
private val fundo = Brush.verticalGradient(
    listOf(
        Color(0xFFFFF3D8),
        Color(0xFFFFE5F3),
        Color(0xFFE7E5FF)
    )
)

@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    onSelecionarTema: (Long) -> Unit = {},
    onVerCartas: (Long) -> Unit = {}
) {
    val estado by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(fundo)
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = rosa
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎮 Memória Play", color = roxo)
                    Text(
                        "Catálogo de temas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B297B)
                    )
                    Text(
                        "🌟 Escolha um tema e divirta-se!",
                        color = roxo
                    )
                }
            }
        }

        item {
            BlocoCatalogo("🔎 Encontre seu tema") {
                OutlinedTextField(
                    value = estado.busca,
                    onValueChange = viewModel::mudarBusca,
                    label = { Text("Buscar tema") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            BlocoCatalogo("🎯 Filtrar temas") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OpcaoCatalogo("Disponíveis", estado.filtro == "Ativos") {
                        viewModel.mudarFiltro("Ativos")
                    }
                    OpcaoCatalogo("Todos", estado.filtro == "Todos") {
                        viewModel.mudarFiltro("Todos")
                    }
                    OpcaoCatalogo("Inativos", estado.filtro == "Inativos") {
                        viewModel.mudarFiltro("Inativos")
                    }
                }
            }
        }

        item {
            BlocoCatalogo("📊 Ordenar por") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OpcaoCatalogo(
                        "Ordem",
                        estado.ordenacao == "Ordem"
                    ) {
                        viewModel.mudarOrdenacao("Ordem")
                    }

                    OpcaoCatalogo(
                        "Nome",
                        estado.ordenacao == "Nome"
                    ) {
                        viewModel.mudarOrdenacao("Nome")
                    }
                }
            }
        }

        when {
            estado.carregando -> item {
                BlocoCatalogo("Carregando temas...") {
                    CircularProgressIndicator(color = roxo)
                }
            }

            estado.erro != null -> item {
                BlocoCatalogo("⚠️ Atenção") {
                    Text(estado.erro.orEmpty(), color = roxo)
                }
            }

            estado.temas.isEmpty() -> item {
                BlocoCatalogo("🧩 Resultados") {
                    Text("Nenhum tema encontrado")
                    Text("Experimente outra busca ou filtro.")
                }
            }

            else -> items(estado.temas, key = { it.id }) { tema ->
                CartaoTemaColorido(
                    tema = tema,
                    selecionado = estado.temaSelecionadoId == tema.id,
                    aoSelecionar = {
                        viewModel.selecionarTema(tema.id)
                        onSelecionarTema(tema.id)
                    },
                    aoVerCartas = { onVerCartas(tema.id) }
                )
            }
        }
    }
}

@Composable
private fun BlocoCatalogo(
    titulo: String,
    conteudo: @Composable () -> Unit
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
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(titulo, fontWeight = FontWeight.Bold, color = roxo)
            conteudo()
        }
    }
}

@Composable
private fun OpcaoCatalogo(
    nome: String,
    selecionado: Boolean,
    clicar: () -> Unit
) {
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
private fun CartaoTemaColorido(
    tema: TemaCatalogo,
    selecionado: Boolean,
    aoSelecionar: () -> Unit,
    aoVerCartas: () -> Unit
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
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "🧩 ${tema.nome}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = roxo
            )

            Text(
                if (tema.ativo) "Disponível para jogar"
                else "Indisponível",
                color = Color(0xFF655473)
            )

            Button(
                onClick = aoSelecionar,
                enabled = tema.ativo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = roxo
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (selecionado) "Jogar novamente" else "Jogar")
            }
            OutlinedButton(onClick = aoVerCartas) {
                Text("Ver categorias e cartas")
            }
        }
    }
}
