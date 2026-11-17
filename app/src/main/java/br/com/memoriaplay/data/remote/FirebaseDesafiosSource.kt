
package br.com.memoriaplay.data.remote

import br.com.memoriaplay.features.desafios.model.Desafio

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDesafiosSource(
    private val consultaSimulada:
    (suspend () -> List<Desafio>)? = null
) {

    suspend fun buscarDesafios(): List<Desafio> {

        // Consulta simulada para testes automatizados
        if (consultaSimulada != null) {
            return consultaSimulada.invoke()
        }

        // Acessa o Firebase Firestore
        val banco = FirebaseFirestore.getInstance()

        // Busca os documentos da coleção desafios
        val resultado = banco
            .collection("desafios")
            .get()
            .await()

        // Converte os documentos em objetos Desafio
        return resultado.documents.map { documento ->

            Desafio(
                id = documento.id,

                titulo =
                    documento.getString("titulo") ?: "",

                descricao =
                    documento.getString("descricao") ?: "",

                quantidadePares =
                    documento.getLong("quantidadePares")
                        ?.toInt() ?: 3
            )
        }
    }
}
