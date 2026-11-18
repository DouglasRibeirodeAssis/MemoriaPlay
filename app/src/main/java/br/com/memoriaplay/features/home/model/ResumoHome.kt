package br.com.memoriaplay.features.home.model

/**
 * Dados agregados exibidos no início. Não contém dependência de Android/Compose.
 */
data class ResumoHome(
    val totalPartidas: Int = 0,
    val totalVitorias: Int = 0,
    val melhorTempoSegundos: Long? = null,
    val ultimaPartidaModo: String? = null
) {
    val possuiPartidas: Boolean get() = totalPartidas > 0
}
