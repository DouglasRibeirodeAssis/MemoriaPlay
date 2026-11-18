package br.com.memoriaplay.features.jogo.model

import br.com.memoriaplay.features.desafios.model.Desafio

data class ConfiguracaoPartida(
    val nivel: NivelJogo,
    val quantidadePares: Int = nivel.paresFixos ?: 3,
    val temaId: Long? = null,
    val jogador: String = "Jogador",
    val desafio: Desafio? = null
) {
    init {
        require(quantidadePares in 3..12)
        require(nivel.paresFixos == null ||
            quantidadePares == nivel.paresFixos)
        require(jogador.isNotBlank())
    }
}
