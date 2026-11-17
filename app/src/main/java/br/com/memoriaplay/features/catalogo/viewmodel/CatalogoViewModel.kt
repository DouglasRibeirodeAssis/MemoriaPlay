package br.com.memoriaplay.features.catalogo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.memoriaplay.features.catalogo.data.CatalogoFonte
import br.com.memoriaplay.features.catalogo.model.TemaCatalogo
import br.com.memoriaplay.features.catalogo.ui.CatalogoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val repositorio: CatalogoFonte
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState
    private var todos = emptyList<TemaCatalogo>()

    init {
        viewModelScope.launch {
            try {
                repositorio.observarTemas().collect { temas ->
                    todos = temas
                    atualizarLista()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = e.message ?: "Erro ao carregar temas"
                )
            }
        }
    }

    fun mudarBusca(texto: String) {
        _uiState.value = _uiState.value.copy(busca = texto)
        atualizarLista()
    }

    fun mudarFiltro(filtro: String) {
        _uiState.value = _uiState.value.copy(filtro = filtro)
        atualizarLista()
    }

    fun mudarOrdenacao(ordenacao: String) {
        _uiState.value = _uiState.value.copy(ordenacao = ordenacao)
        atualizarLista()
    }

    fun selecionarTema(id: Long) {
        if (todos.any { it.id == id && it.ativo }) {
            _uiState.value = _uiState.value.copy(
                temaSelecionadoId = id
            )
        }
    }

    private fun atualizarLista() {
        val estado = _uiState.value
        var lista = todos.filter {
            it.nome.contains(estado.busca, ignoreCase = true)
        }
        lista = when (estado.filtro) {
            "Ativos" -> lista.filter { it.ativo }
            "Inativos" -> lista.filter { !it.ativo }
            else -> lista
        }
        lista = when (estado.ordenacao) {
            "Nome" -> lista.sortedBy { it.nome }
            else -> lista.sortedWith(
                compareBy<TemaCatalogo> { it.ordem }
                    .thenBy { it.nome }
            )
        }
        _uiState.value = estado.copy(
            temas = lista,
            carregando = false,
            erro = null
        )
    }

    companion object {
        fun fabrica(repositorio: CatalogoFonte) = viewModelFactory {
            initializer { CatalogoViewModel(repositorio) }
        }
    }
}
