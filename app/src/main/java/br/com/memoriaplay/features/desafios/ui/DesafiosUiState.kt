
package br.com.memoriaplay.features.desafios.ui

import br.com.memoriaplay.features.desafios.model.Desafio

data class DesafiosUiState(
    val desafios: List<Desafio> = emptyList(),
    val carregando: Boolean = false,
    val mensagemErro: String? = null
)
