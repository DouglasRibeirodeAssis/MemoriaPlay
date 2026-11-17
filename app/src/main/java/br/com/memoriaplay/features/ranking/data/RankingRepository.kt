
package br.com.memoriaplay.features.ranking.data

import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RankingRepository(
    private val partidaDao: PartidaDao
) {

    fun observarRanking(): Flow<List<PartidaEntity>> {
        return partidaDao.observarTodas().map { partidas ->
            partidas
                .filter { it.venceu }
                .sortedWith(
                    compareBy<PartidaEntity> { it.tempoSegundos }
                        .thenBy { it.tentativas }
                )
        }
    }

    fun observarHistorico(): Flow<List<PartidaEntity>> {
        return partidaDao.observarTodas()
    }
}
