package br.com.memoriaplay.config

import androidx.lifecycle.ViewModel
import br.com.memoriaplay.features.config.data.PreferenciasFonte
import br.com.memoriaplay.features.config.data.PreferenciasRepository
import br.com.memoriaplay.features.config.model.EscalaTexto
import br.com.memoriaplay.features.config.model.PreferenciasJogo
import br.com.memoriaplay.features.config.viewmodel.ConfiguracoesViewModel
import br.com.memoriaplay.features.config.viewmodel.ConfiguracoesViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConfiguracoesViewModelTest {

    // Fonte falsa: guarda na memória e pode ser mandada falhar
    private class FonteFalsa(
        inicial: PreferenciasJogo = PreferenciasJogo()
    ) : PreferenciasFonte {
        private val _preferencias = MutableStateFlow(inicial)
        override val preferencias: StateFlow<PreferenciasJogo> = _preferencias
        var falhar = false
        var gravacoes = 0

        override fun atualizar(mudanca: (PreferenciasJogo) -> PreferenciasJogo) {
            if (falhar) throw IllegalStateException("falha simulada")
            gravacoes++
            _preferencias.value = mudanca(_preferencias.value)
        }
    }

    private class OutroViewModel : ViewModel()

    @After
    fun desfazerTesteDoMain() {
        Dispatchers.resetMain()
    }

    private fun TestScope.criarRepositorio(fonte: FonteFalsa): PreferenciasRepository {
        val despachante = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(despachante)
        return PreferenciasRepository(fonte, despachante)
    }

    private fun TestScope.criarViewModel(fonte: FonteFalsa): ConfiguracoesViewModel =
        ConfiguracoesViewModel(criarRepositorio(fonte))

    @Test
    fun estadoInicialUsaOsValoresSalvos() = runTest {
        val salvas = PreferenciasJogo(
            altoContraste = true,
            escalaTexto = EscalaTexto.GRANDE,
            feedbackAcessivel = false
        )
        val viewModel = criarViewModel(FonteFalsa(salvas))
        assertEquals(salvas, viewModel.uiState.value.preferencias)
        assertNull(viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun alterarAltoContrasteSalvaEAtualizaOEstado() = runTest {
        val fonte = FonteFalsa()
        val viewModel = criarViewModel(fonte)
        viewModel.onAltoContrasteChange(true)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.preferencias.altoContraste)
        assertEquals(1, fonte.gravacoes)
        assertNull(viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun alterarEscalaTextoMantemAsOutrasEscolhas() = runTest {
        val viewModel = criarViewModel(FonteFalsa())
        viewModel.onAltoContrasteChange(true)
        advanceUntilIdle()
        viewModel.onEscalaTextoChange(EscalaTexto.MUITO_GRANDE)
        advanceUntilIdle()
        val preferencias = viewModel.uiState.value.preferencias
        assertEquals(EscalaTexto.MUITO_GRANDE, preferencias.escalaTexto)
        assertTrue(preferencias.altoContraste)
        assertTrue(preferencias.feedbackAcessivel)
    }

    @Test
    fun alterarFeedbackAcessivelDesligaOsAvisos() = runTest {
        val viewModel = criarViewModel(FonteFalsa())
        viewModel.onFeedbackAcessivelChange(false)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.preferencias.feedbackAcessivel)
    }

    @Test
    fun doisToquesRapidosNaoPerdemNenhumaEscolha() = runTest {
        val viewModel = criarViewModel(FonteFalsa())
        // dois toques antes de a primeira gravação terminar
        viewModel.onAltoContrasteChange(true)
        viewModel.onEscalaTextoChange(EscalaTexto.GRANDE)
        advanceUntilIdle()
        val preferencias = viewModel.uiState.value.preferencias
        assertTrue(preferencias.altoContraste)
        assertEquals(EscalaTexto.GRANDE, preferencias.escalaTexto)
    }

    @Test
    fun falhaAoSalvarMostraErroEMantemOsValoresAnteriores() = runTest {
        val fonte = FonteFalsa()
        fonte.falhar = true
        val viewModel = criarViewModel(fonte)
        viewModel.onAltoContrasteChange(true)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.preferencias.altoContraste)
        assertNotNull(viewModel.uiState.value.mensagemErro)
        assertEquals(0, fonte.gravacoes)
    }

    @Test
    fun limparErroTiraAMensagemDaTela() = runTest {
        val fonte = FonteFalsa()
        fonte.falhar = true
        val viewModel = criarViewModel(fonte)
        viewModel.onAltoContrasteChange(true)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.mensagemErro)
        viewModel.onLimparErro()
        assertNull(viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun salvarComSucessoDepoisDeUmErroLimpaAMensagem() = runTest {
        val fonte = FonteFalsa()
        fonte.falhar = true
        val viewModel = criarViewModel(fonte)
        viewModel.onAltoContrasteChange(true)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.mensagemErro)

        fonte.falhar = false
        viewModel.onAltoContrasteChange(true)
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.mensagemErro)
        assertTrue(viewModel.uiState.value.preferencias.altoContraste)
    }

    @Test
    fun mudancaFeitaForaDoViewModelApareceNoEstado() = runTest {
        val fonte = FonteFalsa()
        val viewModel = criarViewModel(fonte)
        advanceUntilIdle()
        // mudança feita direto na fonte aparece no estado
        fonte.atualizar { it.copy(escalaTexto = EscalaTexto.MUITO_GRANDE) }
        advanceUntilIdle()
        assertEquals(EscalaTexto.MUITO_GRANDE, viewModel.uiState.value.preferencias.escalaTexto)
    }

    @Test
    fun escolhasContinuamAoCriarUmNovoViewModel() = runTest {
        val fonte = FonteFalsa()
        val primeiro = criarViewModel(fonte)
        primeiro.onAltoContrasteChange(true)
        advanceUntilIdle()
        primeiro.onEscalaTextoChange(EscalaTexto.GRANDE)
        advanceUntilIdle()

        // ViewModel novo com a mesma fonte = app reaberto
        val segundo = criarViewModel(fonte)
        val preferencias = segundo.uiState.value.preferencias
        assertTrue(preferencias.altoContraste)
        assertEquals(EscalaTexto.GRANDE, preferencias.escalaTexto)
    }

    @Test
    fun fabricaCriaOViewModelComORepositorioRecebido() = runTest {
        val salvas = PreferenciasJogo(altoContraste = true)
        val fabrica = ConfiguracoesViewModelFactory(criarRepositorio(FonteFalsa(salvas)))
        val viewModel = fabrica.create(ConfiguracoesViewModel::class.java)
        assertEquals(salvas, viewModel.uiState.value.preferencias)
    }

    @Test
    fun fabricaRejeitaViewModelDesconhecido() = runTest {
        val fabrica = ConfiguracoesViewModelFactory(criarRepositorio(FonteFalsa()))
        assertThrows(IllegalArgumentException::class.java) {
            fabrica.create(OutroViewModel::class.java)
        }
    }
}
