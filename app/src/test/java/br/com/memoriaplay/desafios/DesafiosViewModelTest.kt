
package br.com.memoriaplay.features.desafios.viewmodel

import br.com.memoriaplay.data.remote.FirebaseDesafiosSource
import br.com.memoriaplay.features.desafios.data.DesafiosRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DesafiosViewModelTest {

    private fun criarViewModel(): DesafiosViewModel {


        val fonte = FirebaseDesafiosSource(
            consultaSimulada = {
                throw IllegalStateException("Falha simulada")
            }
        )

        val repository = DesafiosRepository(fonte)

        return DesafiosViewModel(repository)
    }

    @Test
    fun estadoInicialPossuiValoresPadrao() {

        val viewModel = criarViewModel()

        val estado = viewModel.uiState.value

        assertTrue(estado.desafios.isEmpty())

        assertFalse(estado.carregando)

        assertNull(estado.mensagemErro)
    }

    @Test
    fun carregarDesafiosRegistraFalha() = runTest {

        Dispatchers.setMain(
            StandardTestDispatcher(testScheduler)
        )

        try {

            val viewModel = criarViewModel()

            viewModel.carregarDesafios()

            advanceUntilIdle()

            val estado = viewModel.uiState.value

            assertFalse(estado.carregando)

            assertTrue(estado.desafios.isEmpty())

            assertEquals(
                "Não foi possível carregar os desafios.",
                estado.mensagemErro
            )

        } finally {

            Dispatchers.resetMain()
        }
    }

    @Test
    fun tentarNovamentePermiteNovaTentativa() = runTest {

        Dispatchers.setMain(
            StandardTestDispatcher(testScheduler)
        )

        try {

            val viewModel = criarViewModel()

            viewModel.carregarDesafios()

            advanceUntilIdle()

            viewModel.tentarNovamente()

            advanceUntilIdle()

            val estado = viewModel.uiState.value

            assertFalse(estado.carregando)

            assertEquals(
                "Não foi possível carregar os desafios.",
                estado.mensagemErro
            )

        } finally {

            Dispatchers.resetMain()
        }
    }
}
