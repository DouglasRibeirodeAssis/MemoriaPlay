package br.com.memoriaplay.features.catalogo.data

import br.com.memoriaplay.data.local.dao.CartaDao
import br.com.memoriaplay.data.local.dao.CategoriaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.entity.CartaEntity
import br.com.memoriaplay.data.local.entity.CategoriaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.catalogo.model.TemaCatalogo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf

interface CatalogoFonte {
    fun observarTemas(): Flow<List<TemaCatalogo>>
    suspend fun buscarTema(id: Long): TemaCatalogo?
}

class CatalogoRepository(
    private val dao: TemaDao,
    private val categoriaDao: CategoriaDao? = null,
    private val cartaDao: CartaDao? = null
) : CatalogoFonte {
    override fun observarTemas(): Flow<List<TemaCatalogo>> {
        return dao.observarTodos().map { lista ->
            lista.map { it.paraCatalogo() }
        }.catch { e ->
            throw IllegalStateException("Erro ao listar temas", e)
        }
    }

    override suspend fun buscarTema(id: Long): TemaCatalogo? {
        return try {
            dao.buscarPorId(id)?.paraCatalogo()
        } catch (e: Exception) {
            throw IllegalStateException("Erro ao buscar tema", e)
        }
    }

    /** Detalhes reativos para a tela do catálogo; não acessa DAOs pela UI. */
    fun observarCategorias(temaId: Long): Flow<List<CategoriaEntity>> =
        categoriaDao?.observarPorTema(temaId) ?: flowOf(emptyList())

    fun observarCartas(temaId: Long): Flow<List<CartaEntity>> =
        cartaDao?.observarAtivasPorTema(temaId) ?: flowOf(emptyList())

    suspend fun cadastrar(nome: String): Long {
        require(nome.isNotBlank()) { "Nome obrigatório" }
        return try {
            dao.inserir(TemaEntity(nome = nome.trim()))
        } catch (e: Exception) {
            throw IllegalStateException("Erro ao cadastrar tema", e)
        }
    }

    suspend fun editar(tema: TemaCatalogo): Int {
        return try {
            dao.atualizar(tema.paraEntity())
        } catch (e: Exception) {
            throw IllegalStateException("Erro ao editar tema", e)
        }
    }

    suspend fun excluir(tema: TemaCatalogo): Int {
        return try {
            dao.excluir(tema.paraEntity())
        } catch (e: Exception) {
            throw IllegalStateException("Erro ao excluir tema", e)
        }
    }

    private fun TemaEntity.paraCatalogo() = TemaCatalogo(
        id = id,
        nome = nome,
        ativo = ativo,
        ordem = ordem
    )

    private fun TemaCatalogo.paraEntity() = TemaEntity(
        id = id,
        nome = nome,
        ativo = ativo,
        ordem = ordem
    )
}
