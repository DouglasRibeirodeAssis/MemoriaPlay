
package br.com.memoriaplay.features.desafios.data

import br.com.memoriaplay.data.remote.FirebaseDesafiosSource
import br.com.memoriaplay.features.desafios.model.Desafio
import kotlinx.coroutines.CancellationException

class DesafiosRepository(
    private val fonte: FirebaseDesafiosSource
) {

    suspend fun buscarDesafios(): Result<List<Desafio>> {

        return try {

            val desafios = fonte.buscarDesafios()

            Result.success(desafios)

        } catch (erro: CancellationException) {

            throw erro

        } catch (erro: Exception) {

            Result.failure(erro)
        }
    }
}
