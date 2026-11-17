package br.com.memoriaplay.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.TemaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TemaDaoTest {
    private lateinit var banco: AppDatabase

    @Before
    fun iniciar() {
        banco = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun finalizar() {
        banco.close()
    }

    @Test
    fun crudEFiltroDeTemas() = runBlocking {
        val dao = banco.temaDao()
        val id = dao.inserir(TemaEntity(nome = "Animais"))
        val tema = dao.buscarPorId(id)!!
        assertEquals("Animais", tema.nome)

        dao.atualizar(tema.copy(nome = "Bichos", ativo = false))
        assertEquals(0, dao.observarAtivos().first().size)
        assertEquals("Bichos", dao.buscarPorId(id)?.nome)

        dao.excluir(dao.buscarPorId(id)!!)
        assertNull(dao.buscarPorId(id))
        assertEquals(0, dao.observarTodos().first().size)
    }
}
