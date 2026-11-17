package br.com.memoriaplay.catalogo

import br.com.memoriaplay.features.catalogo.data.CatalogoFonte
import br.com.memoriaplay.features.catalogo.model.TemaCatalogo
import br.com.memoriaplay.features.catalogo.viewmodel.CatalogoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest {
    private val temas = MutableStateFlow(
        listOf(
            TemaCatalogo(1, "Animais", true, 2),
            TemaCatalogo(2, "Frutas", true, 1),
            TemaCatalogo(3, "Natureza", false, 3)
        )
    )

    private val fonte = object : CatalogoFonte {
        override fun observarTemas(): Flow<List<TemaCatalogo>> = temas

        override suspend fun buscarTema(id: Long): TemaCatalogo? {
            return temas.value.firstOrNull { it.id == id }
        }
    }

    @Test
    fun filtraTemasAtivos() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val viewModel = CatalogoViewModel(fonte)
            advanceUntilIdle()
            viewModel.mudarFiltro("Ativos")
            assertEquals(2, viewModel.uiState.value.temas.size)
            assertNull(viewModel.uiState.value.erro)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun buscaEOrdenaTemas() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val viewModel = CatalogoViewModel(fonte)
            advanceUntilIdle()
            assertEquals("Frutas", viewModel.uiState.value.temas[0].nome)
            viewModel.mudarOrdenacao("Nome")
            assertEquals("Animais", viewModel.uiState.value.temas[0].nome)
            viewModel.mudarBusca("fru")
            assertEquals("Frutas", viewModel.uiState.value.temas[0].nome)
            viewModel.selecionarTema(2)
            assertEquals(2L, viewModel.uiState.value.temaSelecionadoId)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
