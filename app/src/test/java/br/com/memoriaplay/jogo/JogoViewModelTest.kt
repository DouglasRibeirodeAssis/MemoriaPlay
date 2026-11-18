package br.com.memoriaplay.jogo

import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.jogo.data.JogoDataSource
import br.com.memoriaplay.features.jogo.data.JogoRepository
import br.com.memoriaplay.features.jogo.model.ConfiguracaoPartida
import br.com.memoriaplay.features.jogo.model.NivelJogo
import br.com.memoriaplay.features.jogo.viewmodel.JogoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class FonteJogoFalsa : JogoDataSource {
    val salvas = mutableListOf<PartidaEntity>()
    var falharSalvar = false

    override suspend fun buscarTema(temaId: Long?): TemaEntity? {
        return TemaEntity(id = 1, nome = "Animais")
    }

    override suspend fun inserirPartida(partida: PartidaEntity): Long {
        if (falharSalvar) error("Erro de banco")
        salvas.add(partida)
        return salvas.size.toLong()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class JogoViewModelTest {
    @Before
    fun preparar() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun limpar() {
        Dispatchers.resetMain()
    }

    @Test
    fun terceiraCartaFicaBloqueadaAteResolverTentativa() = runTest {
        val fonte = FonteJogoFalsa()
        val vm = JogoViewModel(
            JogoRepository(fonte),
            agoraMillis = { testScheduler.currentTime }
        )
        vm.iniciarPartida(ConfiguracaoPartida(NivelJogo.FACIL))
        runCurrent()

        val cartas = vm.uiState.value.cartas
        val a = cartas.first()
        val b = cartas.first { it.simbolo != a.simbolo }
        val c = cartas.first { it.id != a.id && it.id != b.id }

        vm.selecionarCarta(a.id)
        vm.selecionarCarta(b.id)
        vm.selecionarCarta(c.id)

        assertEquals(1, vm.uiState.value.tentativas)
        assertTrue(vm.uiState.value.bloqueado)
        assertEquals(2, vm.uiState.value.cartas.count { it.revelada })

        advanceTimeBy(701)
        runCurrent()

        assertFalse(vm.uiState.value.bloqueado)
        assertEquals(0, vm.uiState.value.cartas.count { it.revelada })
        vm.iniciarPartida(ConfiguracaoPartida(NivelJogo.FACIL))
    }

    @Test
    fun tempoParaNaVitoriaESalvaUmaSoVez() = runTest {
        val fonte = FonteJogoFalsa()
        val vm = JogoViewModel(
            JogoRepository(fonte),
            agoraMillis = { testScheduler.currentTime }
        )
        vm.iniciarPartida(ConfiguracaoPartida(NivelJogo.FACIL))
        runCurrent()
        advanceTimeBy(2100)
        runCurrent()
        assertEquals(2, vm.uiState.value.tempoSegundos)

        val grupos = vm.uiState.value.cartas.groupBy { it.simbolo }
        grupos.values.forEach { grupo ->
            vm.selecionarCarta(grupo[0].id)
            vm.selecionarCarta(grupo[1].id)
            advanceTimeBy(701)
            runCurrent()
        }

        val tempoFinal = vm.uiState.value.tempoSegundos
        assertTrue(vm.uiState.value.finalizado)
        assertTrue(vm.uiState.value.resultadoSalvo)
        assertEquals(3, vm.uiState.value.tentativas)
        assertEquals(1, fonte.salvas.size)
        assertEquals("Fácil", fonte.salvas.single().modo)
        assertEquals(1L, fonte.salvas.single().temaId)

        advanceTimeBy(3000)
        runCurrent()
        vm.tentarSalvarNovamente()
        runCurrent()
        assertEquals(tempoFinal, vm.uiState.value.tempoSegundos)
        assertEquals(1, fonte.salvas.size)
    }

    @Test
    fun falhaDePersistenciaPodeSerRepetida() = runTest {
        val fonte = FonteJogoFalsa()
        fonte.falharSalvar = true
        val vm = JogoViewModel(
            JogoRepository(fonte),
            agoraMillis = { testScheduler.currentTime }
        )
        vm.iniciarPartida(ConfiguracaoPartida(NivelJogo.FACIL))
        runCurrent()

        val grupos = vm.uiState.value.cartas.groupBy { it.simbolo }
        grupos.values.forEach { grupo ->
            vm.selecionarCarta(grupo[0].id)
            vm.selecionarCarta(grupo[1].id)
            advanceTimeBy(701)
            runCurrent()
        }
        assertTrue(vm.uiState.value.finalizado)
        assertFalse(vm.uiState.value.resultadoSalvo)
        assertTrue(vm.uiState.value.mensagemErro != null)

        fonte.falharSalvar = false
        vm.tentarSalvarNovamente()
        runCurrent()
        assertTrue(vm.uiState.value.resultadoSalvo)
        assertEquals(1, fonte.salvas.size)
    }
}
