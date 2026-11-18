package br.com.memoriaplay.features.desafios.model

/**
 * Objetivo recebido do Firestore. Limites opcionais: campo ausente ou <= 0
 * significa que não há restrição adicional.
 */
data class Desafio(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val quantidadePares: Int = 3,
    val tempoLimiteSegundos: Int? = null,
    val tentativasMaximas: Int? = null
)
