package br.com.memoriaplay.catalogo

import br.com.memoriaplay.features.catalogo.data.RecursosCartas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RecursosCartasTest {
    @Test fun catalogoPossui72ImagensSemChavesRepetidas() {
        val opcoes = RecursosCartas.opcoes
        assertEquals(72, opcoes.size)
        assertEquals(72, opcoes.map { it.chave }.distinct().size)
        assertNotNull(RecursosCartas.buscar("animal_borboleta"))
        assertNull(RecursosCartas.buscar("imagem_inexistente"))
    }
}
