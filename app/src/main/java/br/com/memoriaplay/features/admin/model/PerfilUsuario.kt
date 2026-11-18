package br.com.memoriaplay.features.admin.model

enum class TipoPerfil {
    JOGADOR,
    ADMIN
}

data class PerfilUsuario(
    val nome: String,
    val tipo: TipoPerfil
) {
    val ehAdmin: Boolean
        get() = tipo == TipoPerfil.ADMIN

    val descricaoTipo: String
        get() = if (ehAdmin) "Administrador" else "Jogador"
}