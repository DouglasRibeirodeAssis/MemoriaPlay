package br.com.memoriaplay.features.desafios.ui

import androidx.compose.material3.MaterialTheme
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

@RunWith(AndroidJUnit4::class)
class DesafiosIniciarTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun botaoIniciarEnviaODesafioCorreto() {
        val desafio = Desafio("missao001", "Missão teste", "Encontre três pares", 3)
        val vm = DesafiosViewModel(DesafiosRepository(
            FirebaseDesafiosSource(consultaSimulada = { listOf(desafio) })
        ))
        var escolhido: Desafio? = null
        composeRule.setContent {
            MaterialTheme {
                DesafiosScreen(vm, aoIniciarDesafio = { escolhido = it })
            }
        }
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Missão teste")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Iniciar desafio").performClick()
        composeRule.runOnIdle { assertEquals("missao001", escolhido?.id) }
    }
}
