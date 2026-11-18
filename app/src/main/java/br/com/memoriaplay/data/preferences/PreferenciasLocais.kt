package br.com.memoriaplay.data.preferences

import android.content.Context
import br.com.memoriaplay.features.config.data.PreferenciasFonte
import br.com.memoriaplay.features.config.model.EscalaTexto
import br.com.memoriaplay.features.config.model.PreferenciasJogo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Guarda as preferências num arquivo privado do app (SharedPreferences). */
class PreferenciasLocais(context: Context) : PreferenciasFonte {

    private val arquivo = context.applicationContext
        .getSharedPreferences("memoriaplay_preferencias", Context.MODE_PRIVATE)

    private val _preferencias = MutableStateFlow(ler())
    override val preferencias: StateFlow<PreferenciasJogo> = _preferencias.asStateFlow()

    // @Synchronized: uma gravação por vez, para dois toques rápidos não se atropelarem
    @Synchronized
    override fun atualizar(mudanca: (PreferenciasJogo) -> PreferenciasJogo) {
        val novas = mudanca(_preferencias.value)
        val gravou = arquivo.edit()
            .putBoolean(CHAVE_CONTRASTE, novas.altoContraste)
            .putString(CHAVE_ESCALA, novas.escalaTexto.name)
            .putBoolean(CHAVE_FEEDBACK, novas.feedbackAcessivel)
            .commit()
        if (!gravou) {
            throw IllegalStateException("Não foi possível gravar o arquivo de preferências.")
        }
        _preferencias.value = novas
    }

    private fun ler(): PreferenciasJogo {
        val nomeSalvo = arquivo.getString(CHAVE_ESCALA, null)
        val escala = EscalaTexto.entries.firstOrNull { it.name == nomeSalvo } ?: EscalaTexto.NORMAL
        return PreferenciasJogo(
            altoContraste = arquivo.getBoolean(CHAVE_CONTRASTE, false),
            escalaTexto = escala,
            feedbackAcessivel = arquivo.getBoolean(CHAVE_FEEDBACK, true)
        )
    }

    private companion object {
        const val CHAVE_CONTRASTE = "alto_contraste"
        const val CHAVE_ESCALA = "escala_texto"
        const val CHAVE_FEEDBACK = "feedback_acessivel"
    }
}
