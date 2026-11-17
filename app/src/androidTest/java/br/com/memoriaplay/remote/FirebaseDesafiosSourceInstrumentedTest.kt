
package br.com.memoriaplay.data.remote

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseDesafiosSourceInstrumentedTest {

    @Test
    fun buscarDesafioRealNoFirebase() = runBlocking {

        val fonte = FirebaseDesafiosSource()

        val desafios = fonte.buscarDesafios()

        val desafio = desafios.firstOrNull {
            it.id == "desafio001"
        }

        assertNotNull(
            "O desafio001 não foi encontrado no Firebase",
            desafio
        )

        assertEquals(
            "Desafio da Memória",
            desafio!!.titulo
        )

        assertEquals(
            6,
            desafio.quantidadePares
        )
    }
}
