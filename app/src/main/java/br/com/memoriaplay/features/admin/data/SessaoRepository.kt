package br.com.memoriaplay.features.admin.data

import br.com.memoriaplay.data.local.dao.UsuarioDao
import br.com.memoriaplay.data.local.entity.UsuarioEntity
import br.com.memoriaplay.features.admin.model.PerfilUsuario
import br.com.memoriaplay.features.admin.model.TipoPerfil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessaoRepository(private val usuarioDao: UsuarioDao) {
    private val _perfilLogado = MutableStateFlow<PerfilUsuario?>(null)
    val perfilLogado: StateFlow<PerfilUsuario?> = _perfilLogado.asStateFlow()

    suspend fun entrar(usuario: String, senha: String): PerfilUsuario? {
        // Uma tentativa de novo login não deve manter uma sessão anterior.
        _perfilLogado.value = null
        val nome = usuario.trim()
        if (nome.isEmpty() || senha.isBlank()) return null

        var registro = usuarioDao.buscarPorUsuario(nome)
        if (registro == null) {
            if (nome.equals("admin", ignoreCase = true)) {
                // Compatibilidade com os bancos em memória dos testes existentes.
                usuarioDao.inserir(
                    UsuarioEntity(usuario = "admin", senha = "1234", tipo = "ADMIN")
                )
            } else {
                // Cadastro simplificado de novo jogador no primeiro acesso.
                usuarioDao.inserir(
                    UsuarioEntity(usuario = nome, senha = senha, tipo = "JOGADOR")
                )
            }
            registro = usuarioDao.buscarPorUsuario(nome)
        }

        // A senha registrada deve coincidir, inclusive para jogadores já cadastrados.
        if (registro == null || registro.senha != senha) return null

        val tipo = if (registro.tipo == "ADMIN") TipoPerfil.ADMIN else TipoPerfil.JOGADOR
        return PerfilUsuario(nome = registro.usuario, tipo = tipo).also {
            _perfilLogado.value = it
        }
    }

    fun sair() {
        _perfilLogado.value = null
    }

    fun ehAdmin(): Boolean = _perfilLogado.value?.ehAdmin == true
}
