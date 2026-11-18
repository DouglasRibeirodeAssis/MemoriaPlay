package br.com.memoriaplay.features.desafios.model

/** Avalia a missão somente depois da partida, sem alterar o ranking normal. */
data class ResultadoDesafio(
    val concluido: Boolean,
    val mensagem: String
)

object AvaliadorDesafio {
    fun avaliar(
        desafio: Desafio,
        paresEncontrados: Int,
        tempoSegundos: Int,
        tentativas: Int
    ): ResultadoDesafio {
        if (desafio.quantidadePares !in 3..12) {
            return ResultadoDesafio(false, "Quantidade de pares fora dos limites do jogo.")
        }
        if (paresEncontrados < desafio.quantidadePares) {
            return ResultadoDesafio(false, "Você ainda não encontrou todos os pares.")
        }
        val tempoLimite = desafio.tempoLimiteSegundos?.takeIf { it > 0 }
        if (tempoLimite != null && tempoSegundos > tempoLimite) {
            return ResultadoDesafio(false, "Tempo limite ultrapassado: $tempoSegundos de $tempoLimite segundos.")
        }
        val maxTentativas = desafio.tentativasMaximas?.takeIf { it > 0 }
        if (maxTentativas != null && tentativas > maxTentativas) {
            return ResultadoDesafio(false, "Tentativas excedidas: $tentativas de $maxTentativas permitidas.")
        }
        return ResultadoDesafio(true, "Você cumpriu todas as regras da missão!")
    }
}
