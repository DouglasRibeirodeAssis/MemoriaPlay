package br.com.memoriaplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.memoriaplay.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {
    @Query("SELECT * FROM partidas ORDER BY dataHora DESC")
    fun observarTodas(): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE modo = :modo ORDER BY dataHora DESC")
    fun observarPorModo(modo: String): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE temaId = :id ORDER BY dataHora DESC")
    fun observarPorTema(id: Long): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE jogador = :nome ORDER BY dataHora DESC")
    fun observarPorJogador(nome: String): Flow<List<PartidaEntity>>

    @Query("SELECT * FROM partidas WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): PartidaEntity?

    @Query("SELECT COUNT(*) FROM partidas")
    fun totalPartidas(): Flow<Int>

    @Query("SELECT COUNT(*) FROM partidas WHERE venceu = 1")
    fun totalVitorias(): Flow<Int>

    @Query("SELECT MIN(tempoSegundos) FROM partidas WHERE venceu = 1")
    fun melhorTempo(): Flow<Int?>

    @Insert
    suspend fun inserir(partida: PartidaEntity): Long

    @Update
    suspend fun atualizar(partida: PartidaEntity): Int

    @Delete
    suspend fun excluir(partida: PartidaEntity): Int
}
