
package br.com.memoriaplay.features.desafios.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

import androidx.test.ext.junit.runners.AndroidJUnit4

import br.com.memoriaplay.data.remote.FirebaseDesafiosSource
import br.com.memoriaplay.features.desafios.data.DesafiosRepository
import br.com.memoriaplay.features.desafios.model.Desafio
import br.com.memoriaplay.features.desafios.viewmodel.DesafiosViewModel

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class DesafiosRecuperacaoTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun recuperarAposFalhaNaConsulta() {

        val contador = AtomicInteger(0)

        val fonte = FirebaseDesafiosSource(
            consultaSimulada = {

                val tentativa = contador.incrementAndGet()

                if (tentativa == 1) {
                    throw IllegalStateException(
                        "Falha simulada na conexão"
                    )
                }

                listOf(
                    Desafio(
                        id = "teste001",
                        titulo = "Desafio recuperado",
                        descricao = "Encontre 6 pares de cartas.",
                        quantidadePares = 6
                    )
                )
            }
        )

        val repository = DesafiosRepository(fonte)
        val viewModel = DesafiosViewModel(repository)

        composeRule.setContent {
            MaterialTheme {
                DesafiosScreen(viewModel)
            }
        }

        // Aguarda a primeira consulta falhar
        composeRule.waitUntil(10_000) {
            composeRule
                .onAllNodesWithText(
                    "Não foi possível carregar os desafios."
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Pressiona o botão para tentar novamente
        composeRule
            .onNodeWithText("Tentar novamente")
            .performClick()

        // Aguarda o desafio ser exibido
        composeRule.waitUntil(10_000) {
            composeRule
                .onAllNodesWithText("Desafio recuperado")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule
            .onNodeWithText("Desafio recuperado")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Pares: 6")
            .assertIsDisplayed()

        // Confirma que duas consultas foram feitas
        assertEquals(2, contador.get())
    }
}
