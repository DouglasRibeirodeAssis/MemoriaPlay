package br.com.memoriaplay.desafios

import br.com.memoriaplay.features.desafios.model.AvaliadorDesafio
import br.com.memoriaplay.features.desafios.model.Desafio
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AvaliadorDesafioTest {
    @Test fun seisParesSemLimiteConclui() {
        val desafio = Desafio(id = "d1", quantidadePares = 6)
        assertTrue(AvaliadorDesafio.avaliar(desafio, 6, 150, 20).concluido)
    }

    @Test fun tempoNoLimiteExatoConclui() {
        val desafio = Desafio(id = "d2", quantidadePares = 3, tempoLimiteSegundos = 60)
        assertTrue(AvaliadorDesafio.avaliar(desafio, 3, 60, 5).concluido)
    }

    @Test fun ultrapassarTempoNaoConclui() {
        val desafio = Desafio(id = "d3", quantidadePares = 3, tempoLimiteSegundos = 60)
        assertFalse(AvaliadorDesafio.avaliar(desafio, 3, 61, 5).concluido)
    }

    @Test fun ultrapassarTentativasNaoConclui() {
        val desafio = Desafio(id = "d4", quantidadePares = 3, tentativasMaximas = 3)
        assertFalse(AvaliadorDesafio.avaliar(desafio, 3, 40, 4).concluido)
    }

    @Test fun faltamParesNaoConclui() {
        val desafio = Desafio(id = "d5", quantidadePares = 8)
        assertFalse(AvaliadorDesafio.avaliar(desafio, 7, 20, 8).concluido)
    }

    @Test fun desafioForaDoIntervaloNaoConclui() {
        val desafio = Desafio(id = "d6", quantidadePares = 24)
        assertFalse(AvaliadorDesafio.avaliar(desafio, 24, 20, 24).concluido)
    }
}
