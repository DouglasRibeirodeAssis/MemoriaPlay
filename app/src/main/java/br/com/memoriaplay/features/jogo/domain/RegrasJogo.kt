package br.com.memoriaplay.features.jogo.domain

import br.com.memoriaplay.features.jogo.model.CartaJogo
import br.com.memoriaplay.features.jogo.model.ImagemCarta
import kotlin.random.Random

object RegrasJogo {
    fun criarCartas(
        imagens: List<ImagemCarta>,
        quantidadePares: Int,
        aleatorio: Random = Random.Default
    ): List<CartaJogo> {
        require(quantidadePares in 3..12)
        val disponiveis = imagens.distinctBy { it.simbolo }
        require(disponiveis.size >= quantidadePares)

        val escolhidas = disponiveis.shuffled(aleatorio)
            .take(quantidadePares)

        return (escolhidas + escolhidas)
            .shuffled(aleatorio)
            .mapIndexed { indice, imagem ->
                CartaJogo(
                    id = indice,
                    simbolo = imagem.simbolo,
                    nome = imagem.nome,
                    imagemRes = imagem.imagemRes
                )
            }
    }

    fun formamPar(primeira: CartaJogo, segunda: CartaJogo): Boolean {
        return primeira.id != segunda.id &&
            primeira.simbolo == segunda.simbolo
    }

    fun venceu(cartas: List<CartaJogo>): Boolean {
        return cartas.isNotEmpty() && cartas.all { it.combinada }
    }
}
