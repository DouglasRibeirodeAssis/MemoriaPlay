package br.com.memoriaplay.features.home.data

import br.com.memoriaplay.features.home.model.ResumoHome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Contrato mínimo de leitura usado pelo módulo Home.
 * O Integrante 3 pode ligar esta fonte ao PartidaDao/Room sem expor DAO à View.
 */
fun interface PartidasHomeDataSource {
    fun observarResumo(): Flow<ResumoHome>
}

class HomeRepository(
    private val fonte: PartidasHomeDataSource = PartidasHomeDataSource { flowOf(ResumoHome()) }
) {
    fun observarResumo(): Flow<ResumoHome> = fonte.observarResumo()
}
