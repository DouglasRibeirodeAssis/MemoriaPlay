package br.com.memoriaplay.features.catalogo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.catalogo.data.RecursosCartas
import br.com.memoriaplay.features.catalogo.viewmodel.DetalhesTemaViewModel

/** Catálogo detalhado: tema -> categorias -> cartas persistidas pelo Room. */
@Composable
fun DetalhesTemaScreen(
    viewModel: DetalhesTemaViewModel,
    aoJogar: () -> Unit,
    aoVoltar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Categorias e cartas", style = MaterialTheme.typography.headlineMedium)
            Text(estado.tema?.nome ?: "Detalhes do tema")
            OutlinedButton(onClick = aoVoltar) { Text("Voltar ao catálogo") }
        }
        if (estado.carregando) item { CircularProgressIndicator() }
        estado.erro?.let { erro -> item { Text(erro, color = MaterialTheme.colorScheme.error) } }
        if (!estado.carregando && estado.categorias.isEmpty()) {
            item { Text("Este tema ainda não possui categorias cadastradas.") }
        }
        estado.categorias.forEach { categoria ->
            item(key = "categoria_${categoria.id}") {
                Text(categoria.nome, style = MaterialTheme.typography.titleLarge)
                if (!categoria.ativo) Text("Categoria inativa")
            }
            val cartas = estado.cartas.filter { it.categoriaId == categoria.id }
            if (cartas.isEmpty()) item(key = "vazio_${categoria.id}") {
                Text("Nenhuma carta ativa nesta categoria.")
            }
            items(cartas, key = { "carta_${it.id}" }) { carta ->
                val imagem = RecursosCartas.buscar(carta.imagemRecurso)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        if (imagem != null) {
                            Image(
                                painter = painterResource(imagem.drawable),
                                contentDescription = carta.nome,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                        }
                        Text(carta.nome, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        item {
            Text("Cartas disponíveis: ${estado.cartas.map { it.imagemRecurso }.distinct().size}")
            Button(
                onClick = aoJogar,
                enabled = estado.tema?.ativo == true &&
                    estado.cartas.map { it.imagemRecurso }.distinct().size >= 3
            ) {
                Text("Jogar com este tema")
            }
            Text("São necessárias pelo menos 3 imagens distintas. Para o nível difícil, 12.")
        }
    }
}
