package br.com.memoriaplay.features.admin.ui

import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.admin.model.PerfilUsuario

data class AdminUiState(
    val usuario: String = "",
    val senha: String = "",
    val perfilLogado: PerfilUsuario? = null,

    val temas: List<TemaEntity> = emptyList(),
    val nomeTema: String = "",
    val ordemTema: String = "0",
    val ativoTema: Boolean = true,
    val temaEmEdicao: TemaEntity? = null,

    val partidas: List<PartidaEntity> = emptyList(),
    val partidaEmEdicao: PartidaEntity? = null,
    val apelidoPartida: String = "",

    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
) {
    val estaLogado: Boolean
        get() = perfilLogado != null

    val ehAdmin: Boolean
        get() = perfilLogado?.ehAdmin == true
}