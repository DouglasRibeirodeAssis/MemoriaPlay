package br.com.memoriaplay.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EstadoVazio(
    titulo: String,
    mensagem: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(mensagem, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}
