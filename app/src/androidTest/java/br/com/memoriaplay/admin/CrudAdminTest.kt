package br.com.memoriaplay.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.features.admin.data.AdminRepository
import br.com.memoriaplay.features.admin.data.SessaoRepository
import br.com.memoriaplay.features.admin.ui.AdminScreen
import br.com.memoriaplay.features.admin.ui.LoginScreen
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrudAdminTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var db: AppDatabase
    private lateinit var repository: AdminRepository

    @Before
    fun criarBanco() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        repository = AdminRepository(db.temaDao(), db.partidaDao())
    }

    @After
    fun fecharBanco() {
        db.close()
    }

    @Test
    fun crudCompletoDeTemas() = runBlocking {
        repository.inserirTema(nome = "Animais", ordem = 1, ativo = true)
        var temas = repository.buscarTemas().first()
        assertEquals(1, temas.size)
        assertEquals("Animais", temas[0].nome)

        repository.atualizarTema(temas[0].copy(nome = "Frutas", ativo = false))
        temas = repository.buscarTemas().first()
        assertEquals("Frutas", temas[0].nome)
        assertEquals(false, temas[0].ativo)

        repository.removerTema(temas[0])
        assertTrue(repository.buscarTemas().first().isEmpty())
    }

    @Test
    fun editarApelidoEExcluirPartida() = runBlocking {
        db.partidaDao().inserir(
            PartidaEntity(
                jogador = "Ana",
                modo = "Clássico",
                quantidadePares = 6,
                tempoSegundos = 30,
                tentativas = 10,
                dataHora = System.currentTimeMillis(),
                nomeTema = "Animais"
            )
        )
        var partidas = repository.buscarPartidas().first()
        assertEquals(1, partidas.size)

        repository.atualizarApelido(partidas[0], "Aninha")
        partidas = repository.buscarPartidas().first()
        assertEquals("Aninha", partidas[0].jogador)
        assertEquals(30, partidas[0].tempoSegundos)

        repository.removerPartida(partidas[0])
        assertTrue(repository.buscarPartidas().first().isEmpty())
    }

    @Test
    fun telaLogin_jogador1234_entraComoJogador() {
        val viewModel = AdminViewModel(repository, SessaoRepository())
        var foiParaJogador = false

        composeRule.setContent {
            LoginScreen(
                viewModel = viewModel,
                aoEntrarComoAdmin = {},
                aoEntrarComoJogador = { foiParaJogador = true }
            )
        }

        composeRule.onNodeWithText("Usuário").performTextInput("jogador")
        composeRule.onNodeWithText("Senha").performTextInput("1234")
        composeRule.onNodeWithText("Entrar").performClick()
        composeRule.waitForIdle()

        assertTrue(foiParaJogador)
        composeRule.onNodeWithText("Perfil: Jogador").assertIsDisplayed()
    }

    @Test
    fun telaAdmin_comPerfilJogador_mostraAcessoRestrito() {
        val sessao = SessaoRepository()
        sessao.entrar("jogador", "1234")
        val viewModel = AdminViewModel(repository, sessao)

        composeRule.setContent {
            AdminScreen(viewModel = viewModel, aoSair = {})
        }

        composeRule.onNodeWithText("Acesso restrito").assertIsDisplayed()
    }

    @Test
    fun telaAdmin_comPerfilAdmin_mostraTemasEPartidas() {
        val sessao = SessaoRepository()
        sessao.entrar("admin", "1234")
        val viewModel = AdminViewModel(repository, sessao)

        composeRule.setContent {
            AdminScreen(viewModel = viewModel, aoSair = {})
        }

        composeRule.onNodeWithText("Temas").assertIsDisplayed()
        composeRule.onNodeWithText("Adicionar tema").assertIsDisplayed()
    }
}