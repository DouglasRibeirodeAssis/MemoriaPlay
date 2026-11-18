package br.com.memoriaplay.features.config.model

enum class EscalaTexto(val fator: Float, val rotulo: String) {
    NORMAL(1.0f, "Normal"),
    GRANDE(1.25f, "Grande"),
    MUITO_GRANDE(1.5f, "Muito grande")
}

/** Escolhas de acessibilidade do jogador. */
data class PreferenciasJogo(
    val altoContraste: Boolean = false,
    val escalaTexto: EscalaTexto = EscalaTexto.NORMAL,
    val feedbackAcessivel: Boolean = true
)
