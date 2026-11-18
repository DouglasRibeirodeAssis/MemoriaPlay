package br.com.memoriaplay.navigation

/**
 * Rotas centralizadas do aplicativo.
 * O argumento modo é mantido na rota de jogo para permitir restaurar o fluxo
 * de navegação sem descartar a configuração selecionada.
 */
object Rotas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val SELECIONAR_MODO = "selecionar_modo"
    const val JOGO = "jogo/{modo}"
    const val JOGO_BASE = "jogo"
    const val RESULTADO = "resultado"
    const val CATALOGO = "catalogo"
    const val RANKING = "ranking"
    const val DESAFIOS = "desafios"
    const val CONFIGURACOES = "configuracoes"
    const val ADMIN = "admin"

    const val ARG_MODO = "modo"

    fun jogo(modo: String): String = "$JOGO_BASE/${modo.trim().ifBlank { "FACIL" }}"

    val destinosPrincipais = listOf(
        INICIO, CATALOGO, RANKING, DESAFIOS, CONFIGURACOES
    )
}
