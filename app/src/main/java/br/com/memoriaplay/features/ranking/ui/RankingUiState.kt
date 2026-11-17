
package br.com.memoriaplay.features.ranking.ui

import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.features.ranking.model.FiltroRanking

data class RankingUiState(
    val filtro: FiltroRanking = FiltroRanking(),
    val carregando: Boolean = false,
    val mensagemErro: String? = null,

    val partidasRanking: List<PartidaEntity> = emptyList(),
    val historico: List<PartidaEntity> = emptyList()
)
