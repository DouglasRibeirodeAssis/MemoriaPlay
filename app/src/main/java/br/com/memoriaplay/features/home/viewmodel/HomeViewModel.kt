package br.com.memoriaplay.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.memoriaplay.features.home.data.HomeRepository
import br.com.memoriaplay.features.home.ui.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarResumo()
    }

    fun carregarResumo() {
        viewModelScope.launch {
            repository.observarResumo()
                .onStart { _uiState.value = _uiState.value.copy(carregando = true, mensagemErro = null) }
                .catch { erro ->
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        mensagemErro = erro.message ?: "Não foi possível carregar o resumo."
                    )
                }
                .collect { resumo ->
                    _uiState.value = HomeUiState(carregando = false, resumo = resumo)
                }
        }
    }
}
