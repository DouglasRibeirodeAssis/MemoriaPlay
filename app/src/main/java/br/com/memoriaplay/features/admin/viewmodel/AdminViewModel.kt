package br.com.memoriaplay.features.admin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.admin.data.AdminRepository
import br.com.memoriaplay.features.admin.data.SessaoRepository
import br.com.memoriaplay.features.admin.ui.AdminUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository,
    private val sessaoRepository: SessaoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessaoRepository.perfilLogado.collect { perfil ->
                _uiState.update { it.copy(perfilLogado = perfil) }
            }
        }

        viewModelScope.launch {
            combine(sessaoRepository.perfilLogado, adminRepository.buscarTemas()) { perfil, temas ->
                if (perfil?.ehAdmin == true) temas else emptyList()
            }
                .catch { e ->
                    Log.e("AdminViewModel", "Erro ao buscar temas: ${e.message}")
                    mostrarErro("Não foi possível carregar os temas.")
                }
                .collect { temas ->
                    _uiState.update { it.copy(temas = temas) }
                }
        }

        viewModelScope.launch {
            combine(sessaoRepository.perfilLogado, adminRepository.buscarPartidas()) { perfil, partidas ->
                if (perfil?.ehAdmin == true) partidas else emptyList()
            }
                .catch { e ->
                    Log.e("AdminViewModel", "Erro ao buscar partidas: ${e.message}")
                    mostrarErro("Não foi possível carregar as partidas.")
                }
                .collect { partidas ->
                    _uiState.update { it.copy(partidas = partidas) }
                }
        }
    }

    fun onUsuarioChange(novoUsuario: String) {
        _uiState.update { it.copy(usuario = novoUsuario) }
    }

    fun onSenhaChange(novaSenha: String) {
        _uiState.update { it.copy(senha = novaSenha) }
    }

    fun onEntrar() {
        val estado = _uiState.value

        if (estado.usuario.isBlank() || estado.senha.isBlank()) {
            mostrarErro("Informe usuário e senha.")
            return
        }

        viewModelScope.launch {
            val perfil = sessaoRepository.entrar(estado.usuario, estado.senha)

            if (perfil == null) {
                mostrarErro("Usuário ou senha inválidos.")
            } else {
                _uiState.update { it.copy(senha = "") }
                mostrarSucesso("Bem-vindo, ${perfil.nome}! Perfil: ${perfil.descricaoTipo}.")
            }
        }
    }

    fun onSair() {
        sessaoRepository.sair()
        _uiState.value = AdminUiState()
    }

    fun onNomeTemaChange(novoNome: String) {
        _uiState.update { it.copy(nomeTema = novoNome) }
    }

    fun onOrdemTemaChange(novaOrdem: String) {
        _uiState.update { it.copy(ordemTema = novaOrdem) }
    }

    fun onAtivoTemaChange(ativo: Boolean) {
        _uiState.update { it.copy(ativoTema = ativo) }
    }

    fun onSalvarTema() {
        if (bloquearSeNaoForAdmin()) return

        val estado = _uiState.value
        val nome = estado.nomeTema.trim()
        val ordem = estado.ordemTema.trim().toIntOrNull()
        val ativo = estado.ativoTema

        if (nome.isEmpty()) {
            mostrarErro("Digite o nome do tema.")
            return
        }

        if (ordem == null) {
            mostrarErro("A ordem precisa ser um número inteiro.")
            return
        }

        val temaEmEdicao = estado.temaEmEdicao

        if (temaEmEdicao == null) {
            executarComoAdmin(
                sucesso = "Tema \"$nome\" adicionado.",
                erro = "Não foi possível adicionar o tema. Tente novamente."
            ) {
                adminRepository.inserirTema(nome, ordem, ativo)
                limparFormularioTema()
            }
        } else {
            executarComoAdmin(
                sucesso = "Tema \"$nome\" atualizado.",
                erro = "Não foi possível atualizar o tema. Tente novamente."
            ) {
                adminRepository.atualizarTema(temaEmEdicao.copy(nome = nome, ordem = ordem, ativo = ativo))
                limparFormularioTema()
            }
        }
    }

    fun onEditarTema(tema: TemaEntity) {
        if (bloquearSeNaoForAdmin()) return

        _uiState.update {
            it.copy(
                temaEmEdicao = tema,
                nomeTema = tema.nome,
                ordemTema = tema.ordem.toString(),
                ativoTema = tema.ativo
            )
        }
    }

    fun onCancelarEdicaoTema() {
        limparFormularioTema()
    }

    fun onExcluirTema(tema: TemaEntity) {
        executarComoAdmin(
            sucesso = "Tema \"${tema.nome}\" excluído.",
            erro = "Não foi possível excluir o tema. Tente novamente."
        ) {
            adminRepository.removerTema(tema)
        }
    }

    fun onEditarPartida(partida: PartidaEntity) {
        if (bloquearSeNaoForAdmin()) return

        _uiState.update {
            it.copy(partidaEmEdicao = partida, apelidoPartida = partida.jogador)
        }
    }

    fun onApelidoPartidaChange(novoApelido: String) {
        _uiState.update { it.copy(apelidoPartida = novoApelido) }
    }

    fun onSalvarApelido() {
        if (bloquearSeNaoForAdmin()) return

        val estado = _uiState.value
        val partida = estado.partidaEmEdicao ?: return
        val apelido = estado.apelidoPartida.trim()

        if (apelido.isEmpty()) {
            mostrarErro("O apelido não pode ficar vazio.")
            return
        }

        executarComoAdmin(
            sucesso = "Apelido alterado para \"$apelido\".",
            erro = "Não foi possível alterar o apelido. Tente novamente."
        ) {
            adminRepository.atualizarApelido(partida, apelido)
            limparFormularioPartida()
        }
    }

    fun onCancelarEdicaoPartida() {
        limparFormularioPartida()
    }

    fun onExcluirPartida(partida: PartidaEntity) {
        executarComoAdmin(
            sucesso = "Partida de \"${partida.jogador}\" excluída.",
            erro = "Não foi possível excluir a partida. Tente novamente."
        ) {
            adminRepository.removerPartida(partida)
        }
    }

    fun onLimparMensagem() {
        _uiState.update { it.copy(mensagemSucesso = null, mensagemErro = null) }
    }

    private fun bloquearSeNaoForAdmin(): Boolean {
        if (sessaoRepository.ehAdmin()) return false

        mostrarErro("Acesso negado: apenas o perfil Administrador pode fazer isso.")
        return true
    }

    private fun executarComoAdmin(
        sucesso: String,
        erro: String,
        acao: suspend () -> Unit
    ) {
        if (bloquearSeNaoForAdmin()) return

        viewModelScope.launch {
            try {
                acao()
                mostrarSucesso(sucesso)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Msg: ${e.message}")
                mostrarErro(erro)
            }
        }
    }

    private fun limparFormularioTema() {
        _uiState.update { it.copy(temaEmEdicao = null, nomeTema = "", ordemTema = "0", ativoTema = true) }
    }

    private fun limparFormularioPartida() {
        _uiState.update { it.copy(partidaEmEdicao = null, apelidoPartida = "") }
    }

    private fun mostrarSucesso(texto: String) {
        _uiState.update { it.copy(mensagemSucesso = texto, mensagemErro = null) }
    }

    private fun mostrarErro(texto: String) {
        _uiState.update { it.copy(mensagemErro = texto, mensagemSucesso = null) }
    }
}

class AdminViewModelFactory(
    private val adminRepository: AdminRepository,
    private val sessaoRepository: SessaoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(adminRepository, sessaoRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}