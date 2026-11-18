package br.com.memoriaplay.features.jogo.ui

import br.com.memoriaplay.features.jogo.model.CartaJogo
import br.com.memoriaplay.features.jogo.model.ConfiguracaoPartida

data class JogoUiState(
    val carregando: Boolean = false,
    val configuracao: ConfiguracaoPartida? = null,
    val nomeTema: String = "",
    val cartas: List<CartaJogo> = emptyList(),
    val primeiraCartaId: Int? = null,
    val bloqueado: Boolean = false,
    val tentativas: Int = 0,
    val paresEncontrados: Int = 0,
    val tempoSegundos: Int = 0,
    val finalizado: Boolean = false,
    val salvando: Boolean = false,
    val resultadoSalvo: Boolean = false,
    val mensagem: String = "",
    val mensagemErro: String? = null
)
