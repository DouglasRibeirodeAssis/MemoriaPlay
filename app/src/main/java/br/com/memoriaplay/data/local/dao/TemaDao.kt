package br.com.memoriaplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.memoriaplay.data.local.entity.TemaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemaDao {
    @Query("SELECT * FROM temas ORDER BY ordem, nome")
    fun observarTodos(): Flow<List<TemaEntity>>

    @Query("SELECT * FROM temas WHERE ativo = 1 ORDER BY ordem, nome")
    fun observarAtivos(): Flow<List<TemaEntity>>

    @Query("""
        SELECT * FROM temas
        WHERE nome LIKE '%' || :busca || '%'
        ORDER BY ordem, nome
    """)
    fun pesquisar(busca: String): Flow<List<TemaEntity>>

    @Query("SELECT * FROM temas WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): TemaEntity?

    @Insert
    suspend fun inserir(tema: TemaEntity): Long

    @Update
    suspend fun atualizar(tema: TemaEntity): Int

    @Delete
    suspend fun excluir(tema: TemaEntity): Int
}
