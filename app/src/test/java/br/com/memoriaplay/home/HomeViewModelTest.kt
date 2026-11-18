package br.com.memoriaplay.home

import br.com.memoriaplay.features.home.data.HomeRepository
import br.com.memoriaplay.features.home.data.PartidasHomeDataSource
import br.com.memoriaplay.features.home.model.ResumoHome
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeViewModelTest {
    @Test
    fun repositoryEmiteResumoDasPartidasSalvas() = runBlocking {
        val resumoEsperado = ResumoHome(
            totalPartidas = 5,
            totalVitorias = 4,
            melhorTempoSegundos = 38,
            ultimaPartidaModo = "MEDIO"
        )
        val repository = HomeRepository(
            PartidasHomeDataSource { flowOf(resumoEsperado) }
        )
        assertEquals(resumoEsperado, repository.observarResumo().first())
    }
}
