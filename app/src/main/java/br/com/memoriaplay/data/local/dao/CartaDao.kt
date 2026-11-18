package br.com.memoriaplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.memoriaplay.data.local.entity.CartaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartaDao {
    @Query("SELECT * FROM cartas ORDER BY categoriaId, ordem, nome")
    fun observarTodas(): Flow<List<CartaEntity>>

    @Query("SELECT * FROM cartas WHERE categoriaId = :categoriaId ORDER BY ordem, nome")
    fun observarPorCategoria(categoriaId: Long): Flow<List<CartaEntity>>

    @Query("""
        SELECT cartas.* FROM cartas
        INNER JOIN categorias ON cartas.categoriaId = categorias.id
        WHERE categorias.temaId = :temaId AND categorias.ativo = 1 AND cartas.ativo = 1
        ORDER BY categorias.ordem, cartas.ordem, cartas.nome
    """)
    fun observarAtivasPorTema(temaId: Long): Flow<List<CartaEntity>>

    @Query("""
        SELECT cartas.* FROM cartas
        INNER JOIN categorias ON cartas.categoriaId = categorias.id
        WHERE categorias.temaId = :temaId AND categorias.ativo = 1 AND cartas.ativo = 1
        ORDER BY categorias.ordem, cartas.ordem, cartas.nome
    """)
    suspend fun buscarAtivasPorTema(temaId: Long): List<CartaEntity>

    @Insert suspend fun inserir(item: CartaEntity): Long
    @Update suspend fun atualizar(item: CartaEntity): Int
    @Delete suspend fun excluir(item: CartaEntity): Int
}
