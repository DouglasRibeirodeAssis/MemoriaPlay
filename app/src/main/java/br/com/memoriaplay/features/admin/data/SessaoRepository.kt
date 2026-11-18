package br.com.memoriaplay.features.admin.data

import br.com.memoriaplay.features.admin.model.PerfilUsuario
import br.com.memoriaplay.features.admin.model.TipoPerfil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessaoRepository {
    private data class UsuarioDemo(
        val usuario: String,
        val senha: String,
        val tipo: TipoPerfil
    )

    private val usuariosDemo = listOf(
        UsuarioDemo(usuario = "admin", senha = "1234", tipo = TipoPerfil.ADMIN),
        UsuarioDemo(usuario = "jogador", senha = "1234", tipo = TipoPerfil.JOGADOR)
    )

    private val _perfilLogado = MutableStateFlow<PerfilUsuario?>(null)
    val perfilLogado: StateFlow<PerfilUsuario?> = _perfilLogado.asStateFlow()

    fun entrar(usuario: String, senha: String): PerfilUsuario? {
        val encontrado = usuariosDemo.find {
            it.usuario == usuario.trim().lowercase() && it.senha == senha
        } ?: return null

        val perfil = PerfilUsuario(nome = encontrado.usuario, tipo = encontrado.tipo)
        _perfilLogado.value = perfil
        return perfil
    }

    fun sair() {
        _perfilLogado.value = null
    }

    fun ehAdmin(): Boolean {
        return _perfilLogado.value?.ehAdmin == true
    }
}