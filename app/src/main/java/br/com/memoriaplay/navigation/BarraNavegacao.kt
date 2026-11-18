package br.com.memoriaplay.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

private data class AbaNavegacao(
    val rota: String,
    val titulo: String,
    val simbolo: String
)

private val abas = listOf(
    AbaNavegacao(Rotas.INICIO, "Início", "⌂"),
    AbaNavegacao(Rotas.CATALOGO, "Catálogo", "▦"),
    AbaNavegacao(Rotas.RANKING, "Ranking", "★"),
    AbaNavegacao(Rotas.DESAFIOS, "Desafios", "◆"),
    AbaNavegacao(Rotas.CONFIGURACOES, "Configurações", "⚙")
)

@Composable
fun BarraNavegacao(
    rotaAtual: String?,
    aoSelecionar: (String) -> Unit
) {
    NavigationBar {
        abas.forEach { aba ->
            NavigationBarItem(
                selected = rotaAtual == aba.rota, onClick = { aoSelecionar(aba.rota) },
                icon = { Text(aba.simbolo, style = androidx.compose.material3.MaterialTheme.typography.titleMedium) },
                label = { Text(aba.titulo, maxLines = 1) }
            )
        }
    }
}
