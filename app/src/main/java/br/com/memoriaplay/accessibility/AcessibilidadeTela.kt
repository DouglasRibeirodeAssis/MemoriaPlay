package br.com.memoriaplay.accessibility

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Área de toque mínima recomendada. */
val TAMANHO_MINIMO_TOQUE = 48.dp

/** Marca o texto como título. */
fun Modifier.tituloAcessivel(): Modifier = semantics { heading() }

/** Faz o leitor de tela falar o texto quando ele muda. Com ativo = false, não faz nada. */
fun Modifier.avisoAoVivo(ativo: Boolean = true, urgente: Boolean = false): Modifier =
    if (!ativo) {
        this
    } else {
        semantics {
            liveRegion = if (urgente) LiveRegionMode.Assertive else LiveRegionMode.Polite
        }
    }

/** Não deixa o componente ficar menor que a área de toque mínima. */
fun Modifier.alvoDeToqueMinimo(): Modifier =
    sizeIn(minWidth = TAMANHO_MINIMO_TOQUE, minHeight = TAMANHO_MINIMO_TOQUE)
