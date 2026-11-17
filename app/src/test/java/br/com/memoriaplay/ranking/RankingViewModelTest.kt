
package br.com.memoriaplay.features.ranking.viewmodel

import br.com.memoriaplay.features.ranking.model.CriterioOrdenacao

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RankingViewModelTest {

    @Test
    fun estadoInicialPossuiFiltrosPadrao() {

        val viewModel = RankingViewModel()

        val filtro = viewModel.uiState.value.filtro

        assertNull(filtro.modo)

        assertNull(filtro.quantidadePares)

        assertEquals(
            CriterioOrdenacao.TEMPO,
            filtro.ordenarPor
        )
    }

    @Test
    fun selecionarModoAtualizaDificuldade() {

        val viewModel = RankingViewModel()

        viewModel.selecionarModo("Fácil")

        val filtro = viewModel.uiState.value.filtro

        assertEquals("Fácil", filtro.modo)
    }

    @Test
    fun selecionarQuantidadeParesAtualizaFiltro() {

        val viewModel = RankingViewModel()

        viewModel.selecionarQuantidadePares(7)

        val filtro = viewModel.uiState.value.filtro

        assertEquals(7, filtro.quantidadePares)
    }

    @Test
    fun selecionarOrdenacaoAtualizaCriterio() {

        val viewModel = RankingViewModel()

        viewModel.selecionarOrdenacao(
            CriterioOrdenacao.TENTATIVAS
        )

        val filtro = viewModel.uiState.value.filtro

        assertEquals(
            CriterioOrdenacao.TENTATIVAS,
            filtro.ordenarPor
        )
    }

    @Test
    fun limparFiltrosRestauraValoresPadrao() {

        val viewModel = RankingViewModel()

        viewModel.selecionarModo("Difícil")

        viewModel.selecionarQuantidadePares(12)

        viewModel.selecionarOrdenacao(
            CriterioOrdenacao.TENTATIVAS
        )

        viewModel.limparFiltros()

        val filtro = viewModel.uiState.value.filtro

        assertNull(filtro.modo)

        assertNull(filtro.quantidadePares)

        assertEquals(
            CriterioOrdenacao.TEMPO,
            filtro.ordenarPor
        )
    }
}
