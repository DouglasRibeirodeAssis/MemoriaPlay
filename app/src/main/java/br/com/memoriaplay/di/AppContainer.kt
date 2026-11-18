package br.com.memoriaplay.di

import android.content.Context
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.features.catalogo.data.CatalogoRepository
import br.com.memoriaplay.features.home.data.HomeRepository
import br.com.memoriaplay.features.home.data.PartidasHomeDataSource
import br.com.memoriaplay.features.home.model.ResumoHome
import kotlinx.coroutines.flow.combine

/** Dependências compartilhadas da aplicação. */
class AppContainer(context: Context) {
    private val banco = AppDatabase.obter(context)

    val catalogoRepository = CatalogoRepository(banco.temaDao())

    val homeRepository = HomeRepository(
        PartidasHomeDataSource {
            combine(
                banco.partidaDao().totalPartidas(),
                banco.partidaDao().totalVitorias(),
                banco.partidaDao().melhorTempo(),
                banco.partidaDao().observarTodas()
            ) { total, vitorias, melhorTempo, partidas ->
                ResumoHome(
                    totalPartidas = total,
                    totalVitorias = vitorias,
                    melhorTempoSegundos = melhorTempo?.toLong(),
                    ultimaPartidaModo = partidas.firstOrNull()?.modo
                )
            }
        }
    )
}
