package br.com.memoriaplay.features.catalogo.model

data class TemaCatalogo(
    val id: Long,
    val nome: String,
    val ativo: Boolean,
    val ordem: Int
)
