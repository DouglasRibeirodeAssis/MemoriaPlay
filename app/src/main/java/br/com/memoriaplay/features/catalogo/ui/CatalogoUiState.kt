package br.com.memoriaplay.features.catalogo.ui

import br.com.memoriaplay.features.catalogo.model.TemaCatalogo

data class CatalogoUiState(
    val temas: List<TemaCatalogo> = emptyList(),
    val busca: String = "",
    val filtro: String = "Todos",
    val ordenacao: String = "Ordem",
    val temaSelecionadoId: Long? = null,
    val carregando: Boolean = true,
    val erro: String? = null
)
