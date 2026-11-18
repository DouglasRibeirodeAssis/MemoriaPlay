package br.com.memoriaplay.features.jogo.model

enum class NivelJogo(
    val titulo: String,
    val paresFixos: Int?
) {
    FACIL("Fácil", 3),
    MEDIO("Médio", 8),
    DIFICIL("Difícil", 12),
    PERSONALIZADO("Personalizado", null);

    companion object {
        fun daRota(valor: String): NivelJogo {
            return entries.firstOrNull {
                it.name.equals(valor, ignoreCase = true)
            } ?: FACIL
        }
    }
}
