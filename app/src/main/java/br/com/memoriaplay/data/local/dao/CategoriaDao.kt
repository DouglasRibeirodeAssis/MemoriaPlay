package br.com.memoriaplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.memoriaplay.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY temaId, ordem, nome")
    fun observarTodas(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE temaId = :temaId ORDER BY ordem, nome")
    fun observarPorTema(temaId: Long): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): CategoriaEntity?

    @Insert suspend fun inserir(item: CategoriaEntity): Long
    @Update suspend fun atualizar(item: CategoriaEntity): Int
    @Delete suspend fun excluir(item: CategoriaEntity): Int
}
