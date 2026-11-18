package br.com.memoriaplay.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.CartaEntity
import br.com.memoriaplay.data.local.entity.CategoriaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Confirma CRUD reativo e exclusão em cascata no Room compartilhado. */
@RunWith(AndroidJUnit4::class)
class CategoriaCartaDaoTest {
    private lateinit var banco: AppDatabase

    @Before fun preparar() {
        banco = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).build()
    }

    @After fun finalizar() { banco.close() }

    @Test fun categoriasECartasReativas_pertencemAoTema() = runBlocking {
        val idTema = banco.temaDao().inserir(TemaEntity(nome = "Esportes"))
        val idCategoria = banco.categoriaDao().inserir(
            CategoriaEntity(temaId = idTema, nome = "Bolas")
        )
        banco.cartaDao().inserir(
            CartaEntity(categoriaId = idCategoria, nome = "Bola", imagemRecurso = "objeto_bola")
        )
        assertEquals("Bolas", banco.categoriaDao().observarPorTema(idTema).first().single().nome)
        assertEquals("Bola", banco.cartaDao().observarAtivasPorTema(idTema).first().single().nome)
        assertEquals(1, banco.cartaDao().buscarAtivasPorTema(idTema).size)
    }

    @Test fun exclusaoDeCategoriaRemoveSuasCartas() = runBlocking {
        val idTema = banco.temaDao().inserir(TemaEntity(nome = "Esportes"))
        val categoria = CategoriaEntity(
            id = banco.categoriaDao().inserir(CategoriaEntity(temaId = idTema, nome = "Bolas")),
            temaId = idTema, nome = "Bolas"
        )
        banco.cartaDao().inserir(
            CartaEntity(categoriaId = categoria.id, nome = "Bola", imagemRecurso = "objeto_bola")
        )
        banco.categoriaDao().excluir(categoria)
        assertTrue(banco.cartaDao().observarTodas().first().isEmpty())
    }

    @Test fun cartaInativaNaoEntraNoBaralho() = runBlocking {
        val temaId = banco.temaDao().inserir(TemaEntity(nome = "Esportes"))
        val categoriaId = banco.categoriaDao().inserir(
            CategoriaEntity(temaId = temaId, nome = "Principal")
        )
        banco.cartaDao().inserir(CartaEntity(
            categoriaId = categoriaId, nome = "Bola", imagemRecurso = "objeto_bola", ativo = false
        ))
        assertTrue(banco.cartaDao().buscarAtivasPorTema(temaId).isEmpty())
    }
}
