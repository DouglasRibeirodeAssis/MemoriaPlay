package br.com.memoriaplay.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PartidaDaoTest {
    private fun partida() = PartidaEntity(
        jogador = "Ana",
        modo = "Fácil",
        quantidadePares = 3,
        tempoSegundos = 35,
        tentativas = 5,
        dataHora = 1000L,
        nomeTema = "Animais"
    )

    @Test
    fun crudDePartidas() = runBlocking {
        val banco = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        try {
            val dao = banco.partidaDao()
            val id = dao.inserir(partida())
            assertEquals(1, dao.observarPorModo("Fácil").first().size)
            assertEquals(1, dao.totalVitorias().first())

            dao.atualizar(dao.buscarPorId(id)!!.copy(tentativas = 7))
            assertEquals(7, dao.buscarPorId(id)?.tentativas)

            dao.excluir(dao.buscarPorId(id)!!)
            assertNull(dao.buscarPorId(id))
        } finally {
            banco.close()
        }
    }

    @Test
    fun partidaPermaneceDepoisDeReabrir() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val nome = "teste_partidas_integrante3.db"
        context.deleteDatabase(nome)
        try {
            var banco = Room.databaseBuilder(
                context, AppDatabase::class.java, nome
            ).build()
            banco.partidaDao().inserir(partida())
            banco.close()

            banco = Room.databaseBuilder(
                context, AppDatabase::class.java, nome
            ).build()
            assertEquals(1, banco.partidaDao().observarTodas().first().size)
            banco.close()
        } finally {
            context.deleteDatabase(nome)
        }
    }
}
