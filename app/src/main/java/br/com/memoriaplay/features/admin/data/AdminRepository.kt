package br.com.memoriaplay.features.admin.data

import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import kotlinx.coroutines.flow.Flow

class AdminRepository(
    private val temaDao: TemaDao,
    private val partidaDao: PartidaDao
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

    fun buscarPartidas(): Flow<List<PartidaEntity>> = partidaDao.observarTodas()

    suspend fun atualizarApelido(partida: PartidaEntity, novoApelido: String) {
        partidaDao.atualizar(partida.copy(jogador = novoApelido))
    }

    suspend fun removerPartida(partida: PartidaEntity) {
        partidaDao.excluir(partida)
    }
}