package br.com.memoriaplay.features.admin.data

import br.com.memoriaplay.data.local.dao.CartaDao
import br.com.memoriaplay.data.local.dao.CategoriaDao
import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.entity.CartaEntity
import br.com.memoriaplay.data.local.entity.CategoriaEntity
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AdminRepository(
    private val temaDao: TemaDao,
    private val partidaDao: PartidaDao,
    private val categoriaDao: CategoriaDao? = null,
    private val cartaDao: CartaDao? = null
) {
    fun buscarTemas(): Flow<List<TemaEntity>> = temaDao.observarTodos()

    suspend fun inserirTema(nome: String, ordem: Int, ativo: Boolean) {
        temaDao.inserir(TemaEntity(nome = nome, ordem = ordem, ativo = ativo))
    }

    suspend fun atualizarTema(tema: TemaEntity) {
        temaDao.atualizar(tema)
    }

    suspend fun removerTema(tema: TemaEntity) {
        temaDao.excluir(tema)
    }

    fun buscarCategorias(): Flow<List<CategoriaEntity>> =
        categoriaDao?.observarTodas() ?: flowOf(emptyList())

    fun buscarCartas(): Flow<List<CartaEntity>> =
        cartaDao?.observarTodas() ?: flowOf(emptyList())

    suspend fun inserirCategoria(temaId: Long, nome: String, ordem: Int, ativo: Boolean) {
        val dao = requireNotNull(categoriaDao) { "Categorias não configuradas" }
        dao.inserir(CategoriaEntity(temaId = temaId, nome = nome, ordem = ordem, ativo = ativo))
    }

    suspend fun atualizarCategoria(categoria: CategoriaEntity) {
        requireNotNull(categoriaDao).atualizar(categoria)
    }

    suspend fun removerCategoria(categoria: CategoriaEntity) {
        requireNotNull(categoriaDao).excluir(categoria)
    }

    suspend fun inserirCarta(categoriaId: Long, nome: String, imagemRecurso: String, ordem: Int, ativo: Boolean) {
        val dao = requireNotNull(cartaDao) { "Cartas não configuradas" }
        dao.inserir(CartaEntity(
            categoriaId = categoriaId, nome = nome, imagemRecurso = imagemRecurso,
            ordem = ordem, ativo = ativo
        ))
    }

    suspend fun atualizarCarta(carta: CartaEntity) {
        requireNotNull(cartaDao).atualizar(carta)
    }

    suspend fun removerCarta(carta: CartaEntity) {
        requireNotNull(cartaDao).excluir(carta)
    }

    fun buscarPartidas(): Flow<List<PartidaEntity>> = partidaDao.observarTodas()

    suspend fun atualizarApelido(partida: PartidaEntity, novoApelido: String) {
        partidaDao.atualizar(partida.copy(jogador = novoApelido))
    }

    suspend fun removerPartida(partida: PartidaEntity) {
        partidaDao.excluir(partida)
    }
}