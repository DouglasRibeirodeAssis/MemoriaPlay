package br.com.memoriaplay.features.home.ui

import br.com.memoriaplay.features.home.model.ResumoHome

data class HomeUiState(
    val carregando: Boolean = true,
    val resumo: ResumoHome = ResumoHome(),
    val mensagemErro: String? = null
)
