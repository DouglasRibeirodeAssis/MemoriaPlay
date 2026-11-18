package br.com.memoriaplay.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RotasTest {
    @Test
    fun rotaDeJogoIncluiModoSelecionado() {
        assertEquals("jogo/DIFICIL", Rotas.jogo("DIFICIL"))
    }

    @Test
    fun existemCincoAbasPrincipais() {
        assertEquals(5, Rotas.destinosPrincipais.size)
        assertTrue(Rotas.destinosPrincipais.contains(Rotas.INICIO))
        assertTrue(Rotas.destinosPrincipais.contains(Rotas.CONFIGURACOES))
    }

    @Test
    fun rotaDesafioMantemIdentificador() {
        assertEquals(
            "selecionar_modo?temaId=-1&desafioId=missao001",
            Rotas.selecionarDesafio("missao001")
        )
    }

    @Test
    fun rotasDefinemDezDestinos() {
        val rotas = setOf(
            Rotas.LOGIN, Rotas.INICIO, Rotas.SELECIONAR_MODO, Rotas.JOGO,
            Rotas.RESULTADO, Rotas.CATALOGO, Rotas.RANKING, Rotas.DESAFIOS,
            Rotas.CONFIGURACOES, Rotas.ADMIN
        )
        assertEquals(10, rotas.size)
    }
}
