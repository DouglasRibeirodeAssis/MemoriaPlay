
package br.com.memoriaplay.features.ranking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.features.ranking.data.RankingRepository
import br.com.memoriaplay.features.ranking.model.CriterioOrdenacao
import br.com.memoriaplay.features.ranking.model.FiltroRanking
import br.com.memoriaplay.features.ranking.ui.RankingUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingViewModel(
    private val repository: RankingRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState = _uiState.asStateFlow()

    private var partidasOriginais: List<PartidaEntity> = emptyList()
    private var historicoOriginal: List<PartidaEntity> = emptyList()

    init {
        if (repository != null) {

            _uiState.update {
                it.copy(carregando = true)
            }

            viewModelScope.launch {

                combine(
                    repository.observarRanking(),
                    repository.observarHistorico()
                ) { ranking, historico ->
                    ranking to historico
                }
                    .catch { erro ->

                        if (erro is CancellationException) {
                            throw erro
                        }

                        _uiState.update {
                            it.copy(
                                carregando = false,
                                mensagemErro = "Erro ao carregar partidas."
                            )
                        }
                    }
                    .collect { (ranking, historico) ->

                        partidasOriginais = ranking
                        historicoOriginal = historico

                        atualizarListas()
                    }
            }
        }
    }

    fun selecionarModo(modo: String?) {
        _uiState.update { estado ->
            estado.copy(
                filtro = estado.filtro.copy(modo = modo)
            )
        }

        atualizarListas()
    }

    fun selecionarQuantidadePares(pares: Int?) {
        _uiState.update { estado ->
            estado.copy(
                filtro = estado.filtro.copy(
                    quantidadePares = pares
                )
            )
        }

        atualizarListas()
    }

    fun selecionarOrdenacao(criterio: CriterioOrdenacao) {
        _uiState.update { estado ->
            estado.copy(
                filtro = estado.filtro.copy(
                    ordenarPor = criterio
                )
            )
        }

        atualizarListas()
    }

    fun limparFiltros() {
        _uiState.update { estado ->
            estado.copy(filtro = FiltroRanking())
        }

        atualizarListas()
    }

    private fun atualizarListas() {

        _uiState.update { estado ->

            val filtro = estado.filtro

            val rankingFiltrado = partidasOriginais.filter {
                correspondeAoFiltro(it, filtro)
            }

            val historicoFiltrado = historicoOriginal.filter {
                correspondeAoFiltro(it, filtro)
            }

            val rankingOrdenado = when (filtro.ordenarPor) {

                CriterioOrdenacao.TEMPO ->
                    rankingFiltrado.sortedWith(
                        compareBy<PartidaEntity> {
                            it.tempoSegundos
                        }.thenBy {
                            it.tentativas
                        }
                    )

                CriterioOrdenacao.TENTATIVAS ->
                    rankingFiltrado.sortedWith(
                        compareBy<PartidaEntity> {
                            it.tentativas
                        }.thenBy {
                            it.tempoSegundos
                        }
                    )
            }

            estado.copy(
                partidasRanking = rankingOrdenado,
                historico = historicoFiltrado,
                carregando = false,
                mensagemErro = null
            )
        }
    }

    private fun correspondeAoFiltro(
        partida: PartidaEntity,
        filtro: FiltroRanking
    ): Boolean {

        val modoCorreto =
            filtro.modo == null ||
                    partida.modo.equals(
                        filtro.modo,
                        ignoreCase = true
                    )

        val paresCorretos =
            filtro.quantidadePares == null ||
                    partida.quantidadePares == filtro.quantidadePares

        return modoCorreto && paresCorretos
    }
}
