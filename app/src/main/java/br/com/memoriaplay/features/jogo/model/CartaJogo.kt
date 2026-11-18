package br.com.memoriaplay.features.jogo.model

data class ImagemCarta(
    val simbolo: String,
    val nome: String,
    val imagemRes: Int
)

data class CartaJogo(
    val id: Int,
    val simbolo: String,
    val nome: String,
    val imagemRes: Int,
    val revelada: Boolean = false,
    val combinada: Boolean = false
)
