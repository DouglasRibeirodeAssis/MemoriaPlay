
package br.com.memoriaplay.features.desafios.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText

import androidx.test.ext.junit.runners.AndroidJUnit4

import br.com.memoriaplay.data.remote.FirebaseDesafiosSource
import br.com.memoriaplay.features.desafios.data.DesafiosRepository
import br.com.memoriaplay.features.desafios.viewmodel.DesafiosViewModel

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DesafiosScreenInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mostrarDesafioRealDoFirebase() {

        // Criar a fonte de dados real
        val fonte = FirebaseDesafiosSource()

        // Criar o Repository
        val repository = DesafiosRepository(fonte)

        // Criar o ViewModel
        val viewModel = DesafiosViewModel(repository)

        // Abrir a tela no emulador
        composeRule.setContent {

            MaterialTheme {
                DesafiosScreen(viewModel)
            }
        }

        // Aguardar o desafio aparecer na tela
        composeRule.waitUntil(30_000) {

            composeRule
                .onAllNodesWithText("Desafio da Memória")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Confirmar que os dados aparecem
        composeRule
            .onNodeWithText("Desafio da Memória")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Pares: 6")
            .assertIsDisplayed()
    }
}
