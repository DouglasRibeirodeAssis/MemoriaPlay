package br.com.memoriaplay.features.jogo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import br.com.memoriaplay.features.jogo.model.CartaJogo
import br.com.memoriaplay.R

@Composable
fun CartaComponente(
    carta: CartaJogo,
    bloqueada: Boolean,
    aoVirar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val aberta = carta.revelada || carta.combinada
    val estado = when {
        carta.combinada -> "par encontrado: ${carta.nome}"
        aberta -> "revelada: ${carta.nome}"
        else -> "fechada"
    }

    Surface(
        onClick = { aoVirar(carta.id) },
        enabled = !aberta && !bloqueada,
        modifier = modifier
            .aspectRatio(0.86f)
            .semantics {
                contentDescription = "Carta ${carta.id + 1}"
                stateDescription = estado
            },
        shape = RoundedCornerShape(14.dp),
        color = when {
            carta.combinada -> Color(0xFFDBF5D4)
            aberta -> Color.White
            else -> Color(0xFFEEE5FF)
        },
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    if (aberta) carta.imagemRes
                    else R.drawable.carta_verso
                ),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
