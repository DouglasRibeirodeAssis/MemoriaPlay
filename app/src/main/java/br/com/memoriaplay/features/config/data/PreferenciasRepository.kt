package br.com.memoriaplay.features.config.data

import br.com.memoriaplay.features.config.model.PreferenciasJogo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/** Quem guarda as preferências (o arquivo no app, ou uma fonte falsa nos testes). */
interface PreferenciasFonte {
    val preferencias: StateFlow<PreferenciasJogo>

    fun atualizar(mudanca: (PreferenciasJogo) -> PreferenciasJogo)
}

/** Única porta de acesso às preferências para o ViewModel. Trata o erro de gravação com try-catch. */
class PreferenciasRepository(
    private val fonte: PreferenciasFonte,
    private val despachante: CoroutineDispatcher = Dispatchers.IO
) {
    val preferencias: StateFlow<PreferenciasJogo> = fonte.preferencias

    suspend fun atualizar(mudanca: (PreferenciasJogo) -> PreferenciasJogo) {
        withContext(despachante) {
            try {
                fonte.atualizar(mudanca)
            } catch (e: Exception) {
                throw IllegalStateException(
                    "Não foi possível salvar as configurações. Tente novamente.",
                    e
                )
            }
        }
    }
}
