package br.com.memoriaplay.admin

import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.dao.UsuarioDao
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.data.local.entity.UsuarioEntity
import br.com.memoriaplay.features.admin.data.AdminRepository
import br.com.memoriaplay.features.admin.data.SessaoRepository
import br.com.memoriaplay.features.admin.model.TipoPerfil
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeTemaDao : TemaDao {
    val tabela = MutableStateFlow<List<TemaEntity>>(emptyList())
    var proximoId = 1L
    var deveFalhar = false

    override fun observarTodos(): Flow<List<TemaEntity>> = tabela

    override fun observarAtivos(): Flow<List<TemaEntity>> =
        tabela.map { lista -> lista.filter { it.ativo } }

    override fun pesquisar(busca: String): Flow<List<TemaEntity>> =
        tabela.map { lista -> lista.filter { it.nome.contains(busca, ignoreCase = true) } }

    override suspend fun buscarPorId(id: Long): TemaEntity? = tabela.value.find { it.id == id }

    override suspend fun inserir(tema: TemaEntity): Long {
        if (deveFalhar) throw RuntimeException("banco indisponível")
        val id = proximoId++
        tabela.value = tabela.value + tema.copy(id = id)
        return id
    }

    override suspend fun atualizar(tema: TemaEntity): Int {
        tabela.value = tabela.value.map { if (it.id == tema.id) tema else it }
        return 1
    }

    override suspend fun excluir(tema: TemaEntity): Int {
        tabela.value = tabela.value.filter { it.id != tema.id }
        return 1
    }
}

class FakePartidaDao : PartidaDao {
    val tabela = MutableStateFlow<List<PartidaEntity>>(emptyList())

    override fun observarTodas(): Flow<List<PartidaEntity>> = tabela

    override fun observarPorJogador(jogador: String): Flow<List<PartidaEntity>> =
        tabela.map { lista -> lista.filter { it.jogador == jogador } }

    override fun observarPorTema(temaId: Long): Flow<List<PartidaEntity>> =
        tabela.map { lista -> lista.filter { it.temaId == temaId } }

    override fun observarPorModo(modo: String): Flow<List<PartidaEntity>> =
        tabela.map { lista -> lista.filter { it.modo == modo } }

    override suspend fun buscarPorId(id: Long): PartidaEntity? = tabela.value.find { it.id == id }

    override fun totalPartidas(): Flow<Int> = tabela.map { it.size }

    override fun totalVitorias(): Flow<Int> = tabela.map { lista -> lista.count { it.venceu } }

    override fun melhorTempo(): Flow<Int?> = tabela.map { lista -> lista.filter { it.venceu }.minOfOrNull { it.tempoSegundos } }

    override suspend fun inserir(partida: PartidaEntity): Long {
        tabela.value = tabela.value + partida
        return partida.id
    }

    override suspend fun atualizar(partida: PartidaEntity): Int {
        tabela.value = tabela.value.map { if (it.id == partida.id) partida else it }
        return 1
    }

    override suspend fun excluir(partida: PartidaEntity): Int {
        tabela.value = tabela.value.filter { it.id != partida.id }
        return 1
    }
}

class FakeUsuarioDao : UsuarioDao {
    val tabela = MutableStateFlow<List<UsuarioEntity>>(listOf(
        UsuarioEntity(id = 1, usuario = "admin", senha = "1234", tipo = "ADMIN"),
        UsuarioEntity(id = 2, usuario = "jogador", senha = "1234", tipo = "JOGADOR")
    ))

    override suspend fun buscarPorUsuario(nome: String): UsuarioEntity? {
        return tabela.value.find { it.usuario.equals(nome, ignoreCase = true) }
    }

    override suspend fun inserir(usuario: UsuarioEntity): Long {
        val novo = usuario.copy(id = (tabela.value.size + 1).toLong())
        tabela.value = tabela.value + novo
        return novo.id
    }

    override suspend fun contar(): Int = tabela.value.size

    override fun observarUsuarios(): Flow<List<UsuarioEntity>> = tabela
}

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {
    private lateinit var temaDao: FakeTemaDao
    private lateinit var partidaDao: FakePartidaDao
    private lateinit var usuarioDao: FakeUsuarioDao
    private lateinit var sessao: SessaoRepository
    private lateinit var viewModel: AdminViewModel

    private val partidaExemplo = PartidaEntity(
        id = 1,
        jogador = "Ana",
        modo = "Clássico",
        quantidadePares = 6,
        tempoSegundos = 45,
        tentativas = 12,
        dataHora = 0L,
        nomeTema = "Animais"
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        temaDao = FakeTemaDao()
        partidaDao = FakePartidaDao()
        partidaDao.tabela.value = listOf(partidaExemplo)
        usuarioDao = FakeUsuarioDao()
        sessao = SessaoRepository(usuarioDao)
        viewModel = AdminViewModel(AdminRepository(temaDao, partidaDao), sessao)
    }

    @After
    fun finalizar() {
        Dispatchers.resetMain()
    }

    private fun entrar(usuario: String) {
        viewModel.onUsuarioChange(usuario)
        viewModel.onSenhaChange("1234")
        viewModel.onEntrar()
    }

    @Test
    fun login_admin_definePerfilAdministrador() {
        entrar("admin")

        assertEquals(TipoPerfil.ADMIN, viewModel.uiState.value.perfilLogado?.tipo)
        assertTrue(viewModel.uiState.value.ehAdmin)
    }

    @Test
    fun login_jogador_definePerfilJogador() {
        entrar("jogador")

        assertEquals(TipoPerfil.JOGADOR, viewModel.uiState.value.perfilLogado?.tipo)
        assertEquals(false, viewModel.uiState.value.ehAdmin)
        assertEquals("Bem-vindo, jogador! Perfil: Jogador.", viewModel.uiState.value.mensagemSucesso)
    }

    @Test
    fun login_jogadorComMaiusculaEEspaco_funciona() {
        viewModel.onUsuarioChange(" Jogador ")
        viewModel.onSenhaChange("1234")
        viewModel.onEntrar()

        assertEquals(TipoPerfil.JOGADOR, viewModel.uiState.value.perfilLogado?.tipo)
    }

    @Test
    fun login_comSenhaErrada_mostraErroCompreensivel() {
        viewModel.onUsuarioChange("admin")
        viewModel.onSenhaChange("errada")
        viewModel.onEntrar()

        assertNull(viewModel.uiState.value.perfilLogado)
        assertEquals("Usuário ou senha inválidos.", viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun jogadorExistente_comSenhaErrada_naoEntra() {
        viewModel.onUsuarioChange("jogador")
        viewModel.onSenhaChange("senha-incorreta")
        viewModel.onEntrar()

        assertNull(viewModel.uiState.value.perfilLogado)
        assertEquals("Usuário ou senha inválidos.", viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun jogador_naoConsegueInserirTema_mesmoChamandoOViewModelDireto() {
        entrar("jogador")

        viewModel.onNomeTemaChange("Animais")
        viewModel.onSalvarTema()

        assertTrue(temaDao.tabela.value.isEmpty())
        assertTrue(viewModel.uiState.value.mensagemErro!!.startsWith("Acesso negado"))
    }

    @Test
    fun jogador_naoConsegueEditarNemExcluirPartida() {
        entrar("jogador")

        viewModel.onExcluirPartida(partidaExemplo)
        viewModel.onEditarPartida(partidaExemplo)

        assertEquals(1, partidaDao.tabela.value.size)
        assertNull(viewModel.uiState.value.partidaEmEdicao)
        assertTrue(viewModel.uiState.value.mensagemErro!!.startsWith("Acesso negado"))
    }

    @Test
    fun jogador_naoRecebeAsListasAdministrativas() {
        entrar("jogador")

        assertTrue(viewModel.uiState.value.partidas.isEmpty())
    }

    @Test
    fun semLogin_acoesDeAdminSaoBloqueadas() {
        viewModel.onExcluirPartida(partidaExemplo)

        assertEquals(1, partidaDao.tabela.value.size)
        assertNotNull(viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun admin_adicionaEditaEExcluiTema() {
        entrar("admin")

        viewModel.onNomeTemaChange("Animais")
        viewModel.onOrdemTemaChange("2")
        viewModel.onSalvarTema()
        assertEquals(listOf("Animais"), viewModel.uiState.value.temas.map { it.nome })
        assertEquals(2, viewModel.uiState.value.temas.first().ordem)

        viewModel.onEditarTema(viewModel.uiState.value.temas.first())
        viewModel.onNomeTemaChange("Animais da fazenda")
        viewModel.onAtivoTemaChange(false)
        viewModel.onSalvarTema()
        assertEquals("Animais da fazenda", viewModel.uiState.value.temas.first().nome)
        assertEquals(false, viewModel.uiState.value.temas.first().ativo)
        assertNull(viewModel.uiState.value.temaEmEdicao)

        viewModel.onExcluirTema(viewModel.uiState.value.temas.first())
        assertTrue(viewModel.uiState.value.temas.isEmpty())
        assertNotNull(viewModel.uiState.value.mensagemSucesso)
    }

    @Test
    fun admin_editaApelidoEExcluiPartida() {
        entrar("admin")
        assertEquals(1, viewModel.uiState.value.partidas.size)

        viewModel.onEditarPartida(partidaExemplo)
        viewModel.onApelidoPartidaChange("Aninha")
        viewModel.onSalvarApelido()
        assertEquals("Aninha", partidaDao.tabela.value.first().jogador)
        assertEquals(45, partidaDao.tabela.value.first().tempoSegundos)

        viewModel.onExcluirPartida(partidaDao.tabela.value.first())
        assertTrue(viewModel.uiState.value.partidas.isEmpty())
    }

    @Test
    fun admin_temaSemNome_mostraErroDeValidacao() {
        entrar("admin")

        viewModel.onNomeTemaChange("   ")
        viewModel.onSalvarTema()

        assertTrue(temaDao.tabela.value.isEmpty())
        assertEquals("Digite o nome do tema.", viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun admin_ordemNaoNumerica_mostraErroDeValidacao() {
        entrar("admin")

        viewModel.onNomeTemaChange("Frutas")
        viewModel.onOrdemTemaChange("abc")
        viewModel.onSalvarTema()

        assertTrue(temaDao.tabela.value.isEmpty())
        assertEquals("A ordem precisa ser um número inteiro.", viewModel.uiState.value.mensagemErro)
    }

    @Test
    fun falhaNoBanco_mostraMensagemCompreensivel() {
        entrar("admin")
        temaDao.deveFalhar = true

        viewModel.onNomeTemaChange("Frutas")
        viewModel.onSalvarTema()

        assertEquals(
            "Não foi possível adicionar o tema. Tente novamente.",
            viewModel.uiState.value.mensagemErro
        )
        assertEquals("Frutas", viewModel.uiState.value.nomeTema)
    }

    @Test
    fun sair_limpaSessaoEBloqueiaNovasAcoes() {
        entrar("admin")
        viewModel.onSair()

        viewModel.onExcluirPartida(partidaExemplo)

        assertNull(viewModel.uiState.value.perfilLogado)
        assertEquals(1, partidaDao.tabela.value.size)
    }
}