package br.com.memoriaplay.features.config.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.memoriaplay.features.config.data.PreferenciasRepository
import br.com.memoriaplay.features.config.model.EscalaTexto
import br.com.memoriaplay.features.config.model.PreferenciasJogo
import br.com.memoriaplay.features.config.ui.ConfiguracoesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado e eventos da tela de Configurações. */
class ConfiguracoesViewModel(
    private val repositorio: PreferenciasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ConfiguracoesUiState(preferencias = repositorio.preferencias.value)
    )
    val uiState: StateFlow<ConfiguracoesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.preferencias.collect { preferencias ->
                _uiState.value = _uiState.value.copy(preferencias = preferencias)
            }
        }
    }

    fun onAltoContrasteChange(ligado: Boolean) {
        aplicar { it.copy(altoContraste = ligado) }
    }

    fun onEscalaTextoChange(escala: EscalaTexto) {
        aplicar { it.copy(escalaTexto = escala) }
    }

    fun onFeedbackAcessivelChange(ligado: Boolean) {
        aplicar { it.copy(feedbackAcessivel = ligado) }
    }

    fun onLimparErro() {
        _uiState.value = _uiState.value.copy(mensagemErro = null)
    }

    private fun aplicar(mudanca: (PreferenciasJogo) -> PreferenciasJogo) {
        viewModelScope.launch {
            try {
                repositorio.atualizar(mudanca)
                _uiState.value = _uiState.value.copy(mensagemErro = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    mensagemErro = e.message ?: "Não foi possível salvar as configurações."
                )
            }
        }
    }
}

/** Cria o ConfiguracoesViewModel com o repositório que ele precisa. */
class ConfiguracoesViewModelFactory(
    private val repositorio: PreferenciasRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfiguracoesViewModel::class.java)) {
            return ConfiguracoesViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel não suportado: ${modelClass.name}")
    }
}
