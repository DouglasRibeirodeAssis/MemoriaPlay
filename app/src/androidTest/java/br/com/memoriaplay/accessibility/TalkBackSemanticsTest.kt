package br.com.memoriaplay.accessibility

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.memoriaplay.R
import br.com.memoriaplay.data.preferences.PreferenciasLocais
import br.com.memoriaplay.features.config.data.PreferenciasFonte
import br.com.memoriaplay.features.config.data.PreferenciasRepository
import br.com.memoriaplay.features.config.model.EscalaTexto
import br.com.memoriaplay.features.config.model.PreferenciasJogo
import br.com.memoriaplay.features.config.ui.ConfiguracoesScreen
import br.com.memoriaplay.features.config.viewmodel.ConfiguracoesViewModel
import br.com.memoriaplay.features.jogo.model.CartaJogo
import br.com.memoriaplay.features.jogo.ui.CartaComponente
import br.com.memoriaplay.ui.theme.MemoriaPlayTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Testes de acessibilidade (o que o TalkBack lê) e de aparência (contraste e tamanho do texto). */
@RunWith(AndroidJUnit4::class)
class TalkBackSemanticsTest {

    @get:Rule
    val regra = createAndroidComposeRule<ComponentActivity>()

    private class FonteEmMemoria : PreferenciasFonte {
        private val _preferencias = MutableStateFlow(PreferenciasJogo())
        override val preferencias: StateFlow<PreferenciasJogo> = _preferencias
        override fun atualizar(mudanca: (PreferenciasJogo) -> PreferenciasJogo) {
            _preferencias.value = mudanca(_preferencias.value)
        }
    }

    private fun criarViewModel() = ConfiguracoesViewModel(
        PreferenciasRepository(FonteEmMemoria(), Dispatchers.Unconfined)
    )

    private fun carta(revelada: Boolean = false, combinada: Boolean = false) = CartaJogo(
        id = 0,
        simbolo = "G",
        nome = "Gato",
        imagemRes = R.drawable.carta_verso,
        revelada = revelada,
        combinada = combinada
    )

    // Cartas do jogo

    @Test
    fun cartaFechadaNaoRevelaOConteudo() {
        regra.setContent {
            CartaComponente(carta = carta(), bloqueada = false, aoVirar = {})
        }
        regra.onNodeWithContentDescription("Carta 1")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "fechada"))
            .assertHasClickAction()
        regra.onNode(hasContentDescription("Gato", substring = true)).assertDoesNotExist()
    }

    @Test
    fun cartaReveladaAnunciaOConteudo() {
        regra.setContent {
            CartaComponente(carta = carta(revelada = true), bloqueada = false, aoVirar = {})
        }
        regra.onNodeWithContentDescription("Carta 1")
            .assert(
                SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "revelada: Gato")
            )
            .assertIsNotEnabled()
    }

    @Test
    fun cartaCombinadaAnunciaParEncontrado() {
        regra.setContent {
            CartaComponente(carta = carta(combinada = true), bloqueada = false, aoVirar = {})
        }
        regra.onNodeWithContentDescription("Carta 1")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    "par encontrado: Gato"
                )
            )
    }

    // Textos de acessibilidade das cartas

    @Test
    fun textosDasCartasNaoRevelamCartaFechada() {
        assertEquals("Carta 3", AcessibilidadeCartas.descricao(3))
        assertEquals(
            "fechada",
            AcessibilidadeCartas.estado(EstadoCartaAcessivel.FECHADA, "Gato")
        )
        assertEquals(
            "revelada: Gato",
            AcessibilidadeCartas.estado(EstadoCartaAcessivel.REVELADA, "Gato")
        )
        assertEquals(
            "par encontrado: Gato",
            AcessibilidadeCartas.estado(EstadoCartaAcessivel.COMBINADA, "Gato")
        )
    }

    @Test
    fun avisoDeVitoriaInformaTentativasETempo() {
        assertEquals(
            "Parabéns! Você encontrou todos os pares em 1 tentativa e 45 segundos.",
            AcessibilidadeCartas.avisoVitoria(tentativas = 1, tempoSegundos = 45)
        )
        assertEquals(
            "Parabéns! Você encontrou todos os pares em 8 tentativas e 1 minuto e 5 segundos.",
            AcessibilidadeCartas.avisoVitoria(tentativas = 8, tempoSegundos = 65)
        )
    }

    @Test
    fun avisosDeParEProgressoSaoClaros() {
        assertEquals("Par encontrado: Gato.", AcessibilidadeCartas.avisoParEncontrado("Gato"))
        assertEquals(
            "Cartas diferentes. Elas foram viradas de volta.",
            AcessibilidadeCartas.avisoParDiferente()
        )
        assertEquals("2 de 6 pares encontrados.", AcessibilidadeCartas.avisoProgresso(2, 6))
    }

    @Test
    fun avisoDeVitoriaEmMinutosExatosNaoFalaZeroSegundos() {
        assertEquals(
            "Parabéns! Você encontrou todos os pares em 2 tentativas e 2 minutos.",
            AcessibilidadeCartas.avisoVitoria(tentativas = 2, tempoSegundos = 120)
        )
    }

    // Tela de Configurações

    @Test
    fun tituloDaTelaEMarcadoComoTitulo() {
        val viewModel = criarViewModel()
        regra.setContent { MemoriaPlayTheme { ConfiguracoesScreen(viewModel) } }
        regra.onNodeWithText("Configurações")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    }

    @Test
    fun altoContrasteTemNomeEstadoEAcaoDeToque() {
        val viewModel = criarViewModel()
        regra.setContent { MemoriaPlayTheme { ConfiguracoesScreen(viewModel) } }

        regra.onNodeWithText("Alto contraste").assertIsOff().assertHasClickAction()
        regra.onNodeWithText("Alto contraste").performScrollTo().performClick()
        regra.waitUntil(5_000) { viewModel.uiState.value.preferencias.altoContraste }
        regra.onNodeWithText("Alto contraste").assertIsOn()
    }

    @Test
    fun escolherUmTamanhoDesmarcaOsOutros() {
        val viewModel = criarViewModel()
        regra.setContent { MemoriaPlayTheme { ConfiguracoesScreen(viewModel) } }

        regra.onNodeWithText("Normal").assertIsOn()
        regra.onNodeWithText("Grande").assertIsOff().performScrollTo().performClick()
        regra.waitUntil(5_000) {
            viewModel.uiState.value.preferencias.escalaTexto == EscalaTexto.GRANDE
        }
        regra.onNodeWithText("Grande").assertIsOn()
        regra.onNodeWithText("Normal").assertIsOff()
    }

    @Test
    fun controlesTemAreaDeToqueDePeloMenos48dp() {
        val viewModel = criarViewModel()
        regra.setContent { MemoriaPlayTheme { ConfiguracoesScreen(viewModel) } }
        regra.onNodeWithText("Alto contraste").assertHeightIsAtLeast(48.dp)
        regra.onNodeWithText("Normal").assertHeightIsAtLeast(48.dp)
        regra.onNodeWithText("Avisos falados do jogo").assertHeightIsAtLeast(48.dp)
    }

    // Tema: contraste e tamanho do texto

    @Test
    fun altoContrasteUsaPretoSobreBranco() {
        var fundo = Color.Unspecified
        var texto = Color.Unspecified
        regra.setContent {
            MemoriaPlayTheme(PreferenciasJogo(altoContraste = true)) {
                fundo = MaterialTheme.colorScheme.background
                texto = MaterialTheme.colorScheme.onBackground
            }
        }
        regra.waitForIdle()
        assertEquals(Color.White, fundo)
        assertEquals(Color.Black, texto)
    }

    @Test
    fun escalaMuitoGrandeMultiplicaOTamanhoDoTexto() {
        var tamanho = TextUnit.Unspecified
        regra.setContent {
            MemoriaPlayTheme(PreferenciasJogo(escalaTexto = EscalaTexto.MUITO_GRANDE)) {
                tamanho = MaterialTheme.typography.bodyLarge.fontSize
            }
        }
        regra.waitForIdle()
        // bodyLarge padrão = 16sp; 16 x 1,5 = 24
        assertEquals(24f, tamanho.value, 0.01f)
    }
}

/** Escolhas gravadas continuam quando o app é aberto de novo. */
@RunWith(AndroidJUnit4::class)
class PersistenciaPreferenciasTest {

    private val contexto: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun limparAntes() = limparArquivo()

    @After
    fun limparDepois() = limparArquivo()

    private fun limparArquivo() {
        contexto.getSharedPreferences("memoriaplay_preferencias", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun primeiraAberturaUsaOsValoresPadrao() {
        assertEquals(PreferenciasJogo(), PreferenciasLocais(contexto).preferencias.value)
    }

    @Test
    fun escolhasContinuamDepoisDeReabrir() {
        val escolhas = PreferenciasJogo(
            altoContraste = true,
            escalaTexto = EscalaTexto.GRANDE,
            feedbackAcessivel = false
        )
        PreferenciasLocais(contexto).atualizar { escolhas }

        // instância nova lê o arquivo de novo (app reaberto)
        assertEquals(escolhas, PreferenciasLocais(contexto).preferencias.value)
    }

    @Test
    fun nomeDeTamanhoDesconhecidoVoltaParaNormal() {
        contexto.getSharedPreferences("memoriaplay_preferencias", Context.MODE_PRIVATE)
            .edit().putString("escala_texto", "GIGANTE").commit()
        assertEquals(
            EscalaTexto.NORMAL,
            PreferenciasLocais(contexto).preferencias.value.escalaTexto
        )
    }
}
