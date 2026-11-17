
package br.com.memoriaplay.features.ranking.viewmodel

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.test.junit4.createComposeRule
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.features.ranking.data.RankingRepository
import br.com.memoriaplay.features.ranking.ui.RankingScreen
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class RankingViewModelRoomTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var banco: AppDatabase

    @Before
    fun preparar() {
        val contexto = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        banco = Room.inMemoryDatabaseBuilder(
            contexto,
            AppDatabase::class.java
        ).build()
    }

    @After
    fun finalizar() {
        banco.close()
    }

    @Test
    fun viewModelRecebeEFiltraPartidasDoRoom() {

        runBlocking {
            banco.partidaDao().inserir(
                criarPartida("Ana", "Fácil", 35)
            )

            banco.partidaDao().inserir(
                criarPartida("Pedro", "Médio", 80)
            )
        }

        val repository = RankingRepository(
            banco.partidaDao()
        )

        val viewModel = RankingViewModel(repository)

        composeRule.setContent {
            RankingScreen(viewModel = viewModel)
        }

        composeRule.waitUntil(10000) {
            viewModel.uiState.value.partidasRanking.size == 2
        }

        assertEquals(
            2,
            viewModel.uiState.value.partidasRanking.size
        )

        composeRule.runOnIdle {
            viewModel.selecionarModo("Fácil")
        }

        composeRule.waitUntil(10000) {
            viewModel.uiState.value.partidasRanking.size == 1
        }

        val resultado =
            viewModel.uiState.value.partidasRanking

        assertEquals(1, resultado.size)
        assertEquals("Ana", resultado[0].jogador)
    }

    private fun criarPartida(
        jogador: String,
        modo: String,
        tempo: Int
    ): PartidaEntity {

        return PartidaEntity(
            jogador = jogador,
            modo = modo,
            quantidadePares = 3,
            tempoSegundos = tempo,
            tentativas = 7,
            dataHora = System.currentTimeMillis(),
            nomeTema = "Animais",
            venceu = true
        )
    }
}
