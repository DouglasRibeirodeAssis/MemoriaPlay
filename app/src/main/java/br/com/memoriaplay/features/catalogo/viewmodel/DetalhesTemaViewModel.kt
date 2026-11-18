package br.com.memoriaplay.features.catalogo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.memoriaplay.data.local.entity.CartaEntity
import br.com.memoriaplay.data.local.entity.CategoriaEntity
import br.com.memoriaplay.features.catalogo.data.CatalogoRepository
import br.com.memoriaplay.features.catalogo.model.TemaCatalogo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class DetalhesTemaUiState(
    val tema: TemaCatalogo? = null,
    val categorias: List<CategoriaEntity> = emptyList(),
    val cartas: List<CartaEntity> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)

class DetalhesTemaViewModel(
    private val repositorio: CatalogoRepository,
    private val temaId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetalhesTemaUiState())
    val uiState: StateFlow<DetalhesTemaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repositorio.observarTemas(),
                repositorio.observarCategorias(temaId),
                repositorio.observarCartas(temaId)
            ) { temas, categorias, cartas ->
                DetalhesTemaUiState(
                    tema = temas.firstOrNull { it.id == temaId },
                    categorias = categorias,
                    cartas = cartas,
                    carregando = false
                )
            }.catch { erro ->
                _uiState.value = DetalhesTemaUiState(
                    carregando = false,
                    erro = erro.message ?: "Não foi possível carregar as cartas"
                )
            }.collect { _uiState.value = it }
        }
    }

    companion object {
        fun fabrica(repositorio: CatalogoRepository, temaId: Long) = viewModelFactory {
            initializer { DetalhesTemaViewModel(repositorio, temaId) }
        }
    }
}
