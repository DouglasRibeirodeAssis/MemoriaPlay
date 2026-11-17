
package br.com.memoriaplay.features.ranking.ui


import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.features.ranking.data.RankingRepository
import br.com.memoriaplay.features.ranking.viewmodel.RankingViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RankingScreenRoomTest {

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
    fun telaMostraJogadoresEAplicaFiltroFacil() {

        runBlocking {

            banco.partidaDao().inserir(
                criarPartida(
                    jogador = "Ana",
                    modo = "Fácil",
                    pares = 3,
                    tempo = 35,
                    venceu = true
                )
            )

            banco.partidaDao().inserir(
                criarPartida(
                    jogador = "Pedro",
                    modo = "Médio",
                    pares = 8,
                    tempo = 80,
                    venceu = true
                )
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

        composeRule.onNodeWithText(
            "1 º - Ana"
        ).assertExists()

        composeRule.onNodeWithText(
            "Fácil"
        ).performClick()

        composeRule.waitUntil(10000) {
            viewModel.uiState.value.partidasRanking.size == 1 &&
                    viewModel.uiState.value.filtro.modo == "Fácil"
        }

        composeRule.onNodeWithText(
            "1 º - Ana"
        ).assertExists()

        composeRule.onNodeWithText(
            "2 º - Pedro"
        ).assertDoesNotExist()
    }

    @Test
    fun historicoMostraPartidaSemVitoria() {

        runBlocking {

            banco.partidaDao().inserir(
                criarPartida(
                    jogador = "Carlos",
                    modo = "Fácil",
                    pares = 3,
                    tempo = 120,
                    venceu = false
                )
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
            viewModel.uiState.value.historico.size == 1 &&
                    !viewModel.uiState.value.carregando
        }

        composeRule.onNodeWithText(
            "Nenhuma vitória encontrada para os filtros selecionados."
        ).assertExists()

        composeRule.onNodeWithText(
            "Histórico"
        ).performClick()

        composeRule.onNodeWithText(
            "Carlos"
        ).assertExists()

        composeRule.onNodeWithText(
            "Resultado: Sem vitória"
        ).assertExists()
    }

    private fun criarPartida(
        jogador: String,
        modo: String,
        pares: Int,
        tempo: Int,
        venceu: Boolean
    ): PartidaEntity {

        return PartidaEntity(
            jogador = jogador,
            modo = modo,
            quantidadePares = pares,
            tempoSegundos = tempo,
            tentativas = 7,
            dataHora = System.currentTimeMillis(),
            nomeTema = "Animais",
            venceu = venceu
        )
    }
}
