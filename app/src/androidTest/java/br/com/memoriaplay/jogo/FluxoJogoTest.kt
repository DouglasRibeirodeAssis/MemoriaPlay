package br.com.memoriaplay.jogo

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.jogo.data.JogoDataSource
import br.com.memoriaplay.features.jogo.data.JogoRepository
import br.com.memoriaplay.features.jogo.model.ConfiguracaoPartida
import br.com.memoriaplay.features.jogo.model.NivelJogo
import br.com.memoriaplay.features.jogo.ui.JogoScreen
import br.com.memoriaplay.features.jogo.ui.ResultadoScreen
import br.com.memoriaplay.features.jogo.ui.SelecionarModoScreen
import br.com.memoriaplay.features.jogo.viewmodel.JogoViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FluxoJogoTest {
    @get:Rule
    val regra = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun jogaFacilEAbreResultadoComPartidaSalva() {
        val fonte = FonteInstrumentada()
        val viewModel = JogoViewModel(JogoRepository(fonte))
        var tela by mutableStateOf("modos")

        regra.setContent {
            MaterialTheme {
                when (tela) {
                    "modos" -> SelecionarModoScreen(
                        aoSelecionarNivel = { nivel, pares ->
                            viewModel.iniciarPartida(
                                ConfiguracaoPartida(
                                    NivelJogo.daRota(nivel),
                                    pares
                                )
                            )
                            tela = "jogo"
                        }
                    )
                    "jogo" -> JogoScreen(
                        viewModel = viewModel,
                        aoVoltar = { tela = "modos" },
                        aoConcluir = { tela = "resultado" }
                    )
                    else -> ResultadoScreen(
                        viewModel = viewModel,
                        aoJogarNovamente = { tela = "modos" },
                        aoVerRanking = { tela = "modos" },
                        aoVoltar = { tela = "modos" }
                    )
                }
            }
        }

        regra.onAllNodesWithText("Jogar")[0].performClick()
        regra.waitUntil(10_000) {
            viewModel.uiState.value.cartas.size == 6
        }

        val pares = viewModel.uiState.value.cartas
            .groupBy { it.simbolo }
            .values

        pares.forEach { cartas ->
            regra.onNodeWithContentDescription(
                "Carta ${cartas[0].id + 1}"
            ).performClick()
            regra.onNodeWithContentDescription(
                "Carta ${cartas[1].id + 1}"
            ).performClick()
        }

        regra.waitUntil(10_000) {
            tela == "resultado" && viewModel.uiState.value.resultadoSalvo
        }
        regra.onNodeWithText("🏆 Resultado da partida")
            .assertIsDisplayed()
        assertEquals(1, fonte.salvas.size)
        assertEquals(3, fonte.salvas.first().quantidadePares)
    }
}

private class FonteInstrumentada : JogoDataSource {
    val salvas = mutableListOf<PartidaEntity>()

    override suspend fun buscarTema(temaId: Long?): TemaEntity? {
        return TemaEntity(id = 1L, nome = "Animais")
    }

    override suspend fun inserirPartida(partida: PartidaEntity): Long {
        salvas.add(partida)
        return salvas.size.toLong()
    }
}
