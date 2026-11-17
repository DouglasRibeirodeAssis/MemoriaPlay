package br.com.memoriaplay.features.ranking.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RankingRepositoryInstrumentedTest {

    private lateinit var banco: AppDatabase
    private lateinit var repository: RankingRepository

    @Before
    fun preparar() {
        val contexto =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext

        banco = Room.inMemoryDatabaseBuilder(
            contexto,
            AppDatabase::class.java
        ).build()

        repository = RankingRepository(
            banco.partidaDao()
        )
    }

    @After
    fun finalizar() {
        banco.close()
    }

    @Test
    fun rankingMostraApenasVitoriasOrdenadasPorTempo() = runBlocking {

        banco.partidaDao().inserir(
            criarPartida("Pedro", 80, 10, 1L, true)
        )

        banco.partidaDao().inserir(
            criarPartida("Ana", 35, 7, 2L, true)
        )

        banco.partidaDao().inserir(
            criarPartida("Carlos", 20, 5, 3L, false)
        )

        val resultado =
            repository.observarRanking().first()

        Assert.assertEquals(2, resultado.size)

        Assert.assertEquals(
            listOf("Ana", "Pedro"),
            resultado.map { it.jogador }
        )
    }

    @Test
    fun historicoMostraTodasAsPartidas() = runBlocking {

        banco.partidaDao().inserir(
            criarPartida("Ana", 35, 7, 1L, true)
        )

        banco.partidaDao().inserir(
            criarPartida("Pedro", 80, 10, 2L, false)
        )

        val resultado =
            repository.observarHistorico().first()

        Assert.assertEquals(2, resultado.size)

        Assert.assertEquals(
            listOf("Pedro", "Ana"),
            resultado.map { it.jogador }
        )
    }

    private fun criarPartida(
        jogador: String,
        tempo: Int,
        tentativas: Int,
        dataHora: Long,
        venceu: Boolean
    ): PartidaEntity {

        return PartidaEntity(
            jogador = jogador,
            modo = "Fácil",
            quantidadePares = 3,
            tempoSegundos = tempo,
            tentativas = tentativas,
            dataHora = dataHora,
            nomeTema = "Animais",
            venceu = venceu
        )
    }
}