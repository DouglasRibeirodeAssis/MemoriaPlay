
package br.com.memoriaplay.features.ranking.model

enum class CriterioOrdenacao {
    TEMPO,
    TENTATIVAS
}

data class FiltroRanking(
    val modo: String? = null,
    val quantidadePares: Int? = null,
    val ordenarPor: CriterioOrdenacao = CriterioOrdenacao.TEMPO
)
