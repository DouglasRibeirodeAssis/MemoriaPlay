package br.com.memoriaplay.jogo

import br.com.memoriaplay.features.jogo.domain.RegrasJogo
import br.com.memoriaplay.features.jogo.model.ImagemCarta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RegrasJogoTest {
    private val imagens = (1..12).map {
        ImagemCarta("simbolo$it", "Imagem $it", it)
    }

    @Test
    fun todosOsModosFormamParesCorretos() {
        for (pares in 3..12) {
            val cartas = RegrasJogo.criarCartas(imagens, pares, Random(7))

            assertEquals(pares * 2, cartas.size)
            assertEquals(cartas.size, cartas.map { it.id }.distinct().size)
            assertEquals(pares, cartas.map { it.simbolo }.distinct().size)
            assertTrue(
                cartas.groupBy { it.simbolo }
                    .values.all { it.size == 2 }
            )
        }
    }

    @Test
    fun identificaParesDiferentesEVitoria() {
        val cartas = RegrasJogo.criarCartas(imagens, 3, Random(9))
        val primeira = cartas.first()
        val par = cartas.first {
            it.simbolo == primeira.simbolo && it.id != primeira.id
        }
        val diferente = cartas.first { it.simbolo != primeira.simbolo }

        assertTrue(RegrasJogo.formamPar(primeira, par))
        assertFalse(RegrasJogo.formamPar(primeira, diferente))
        assertFalse(RegrasJogo.formamPar(primeira, primeira))
        assertFalse(RegrasJogo.venceu(cartas))
        assertTrue(
            RegrasJogo.venceu(cartas.map { it.copy(combinada = true) })
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejeitaQuantidadeDeParesForaDoLimite() {
        RegrasJogo.criarCartas(imagens, 13)
    }
}
