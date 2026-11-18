package br.com.memoriaplay.navigation

object Rotas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val SELECIONAR_MODO = "selecionar_modo"
    const val SELECIONAR_MODO_COM_TEMA =
        "selecionar_modo?temaId={temaId}&desafioId={desafioId}"
    const val JOGO = "jogo/{modo}?pares={pares}&temaId={temaId}"
    const val JOGO_BASE = "jogo"
    const val RESULTADO = "resultado"
    const val CATALOGO = "catalogo"
    const val DETALHES_TEMA = "catalogo/detalhes/{temaId}"
    fun detalhesTema(id: Long): String = "catalogo/detalhes/$id"
    const val RANKING = "ranking"
    const val DESAFIOS = "desafios"
    const val CONFIGURACOES = "configuracoes"
    const val ADMIN = "admin"

    const val ARG_MODO = "modo"
    const val ARG_PARES = "pares"
    const val ARG_TEMA = "temaId"
    const val ARG_DESAFIO = "desafioId"

    fun selecionarDesafio(id: String): String =
        "$SELECIONAR_MODO?temaId=-1&desafioId=${java.net.URLEncoder.encode(id, "UTF-8")}"

    fun selecionarModo(temaId: Long? = null): String {
        return if (temaId == null) SELECIONAR_MODO
        else "$SELECIONAR_MODO?temaId=$temaId"
    }

    fun jogo(
        modo: String,
        quantidadePares: Int? = null,
        temaId: Long? = null
    ): String {
        val base = "$JOGO_BASE/${modo.trim().ifBlank { "FACIL" }}"
        if (quantidadePares == null && temaId == null) return base
        val pares = quantidadePares ?: 3
        return "$base?pares=$pares&temaId=${temaId ?: -1L}"
    }

    val destinosPrincipais = listOf(
        INICIO, CATALOGO, RANKING, DESAFIOS, CONFIGURACOES
    )
}
