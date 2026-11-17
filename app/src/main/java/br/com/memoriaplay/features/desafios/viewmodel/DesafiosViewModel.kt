
package br.com.memoriaplay.features.desafios.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import br.com.memoriaplay.features.desafios.data.DesafiosRepository
import br.com.memoriaplay.features.desafios.ui.DesafiosUiState

import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DesafiosViewModel(
    private val repository: DesafiosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DesafiosUiState()
    )

    val uiState: StateFlow<DesafiosUiState> =
        _uiState.asStateFlow()

    fun carregarDesafios() {

        if (_uiState.value.carregando) {
            return
        }

        viewModelScope.launch {

            // Informa que o carregamento começou
            _uiState.update { estado ->
                estado.copy(
                    carregando = true,
                    mensagemErro = null
                )
            }

            // Solicita os desafios ao Repository
            val resultado = repository.buscarDesafios()

            resultado.fold(

                onSuccess = { lista ->

                    _uiState.update { estado ->
                        estado.copy(
                            desafios = lista,
                            carregando = false,
                            mensagemErro = null
                        )
                    }
                },

                onFailure = { _ ->

                    _uiState.update { estado ->
                        estado.copy(
                            desafios = emptyList(),
                            carregando = false,
                            mensagemErro =
                                "Não foi possível carregar os desafios."
                        )
                    }
                }
            )
        }
    }

    fun tentarNovamente() {
        carregarDesafios()
    }
}
