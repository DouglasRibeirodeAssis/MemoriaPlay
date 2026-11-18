package br.com.memoriaplay.features.admin.data

import br.com.memoriaplay.data.local.dao.UsuarioDao
import br.com.memoriaplay.data.local.entity.UsuarioEntity
import br.com.memoriaplay.features.admin.model.PerfilUsuario
import br.com.memoriaplay.features.admin.model.TipoPerfil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessaoRepository(
    private val usuarioDao: UsuarioDao
) {
    private val _perfilLogado = MutableStateFlow<PerfilUsuario?>(null)
    val perfilLogado: StateFlow<PerfilUsuario?> = _perfilLogado.asStateFlow()

    suspend fun entrar(usuario: String, senha: String): PerfilUsuario? {
        val nomeTrim = usuario.trim()
        if (nomeTrim.isBlank()) return null

        val usuarioLower = nomeTrim.lowercase()

        // Buscar usuário no banco
        var entity = usuarioDao.buscarPorUsuario(nomeTrim)

        if (usuarioLower == "admin") {
            if (entity == null) {
                usuarioDao.inserir(UsuarioEntity(usuario = "admin", senha = "1234", tipo = "ADMIN"))
                entity = usuarioDao.buscarPorUsuario("admin")
            }
            if (entity?.senha == senha) {
                val perfil = PerfilUsuario(nome = entity.usuario, tipo = TipoPerfil.ADMIN)
                _perfilLogado.value = perfil
                return perfil
            }
            return null
        }

        // Para jogador / custom player
        if (entity == null) {
            usuarioDao.inserir(UsuarioEntity(usuario = nomeTrim, senha = senha.ifBlank { "1234" }, tipo = "JOGADOR"))
            entity = usuarioDao.buscarPorUsuario(nomeTrim)
        }

        if (entity != null) {
            val tipo = if (entity.tipo == "ADMIN") TipoPerfil.ADMIN else TipoPerfil.JOGADOR
            val perfil = PerfilUsuario(nome = entity.usuario, tipo = tipo)
            _perfilLogado.value = perfil
            return perfil
        }

        return null
    }

    fun sair() {
        _perfilLogado.value = null
    }

    fun ehAdmin(): Boolean {
        return _perfilLogado.value?.ehAdmin == true
    }
}
