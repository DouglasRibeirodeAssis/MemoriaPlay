package br.com.memoriaplay.features.config.ui

import br.com.memoriaplay.features.config.model.PreferenciasJogo

/** Estado da tela de Configurações. */
data class ConfiguracoesUiState(
    val preferencias: PreferenciasJogo = PreferenciasJogo(),
    val mensagemErro: String? = null
)
