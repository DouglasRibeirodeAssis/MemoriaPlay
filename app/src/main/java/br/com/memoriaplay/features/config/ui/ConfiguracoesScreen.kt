package br.com.memoriaplay.features.config.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.memoriaplay.accessibility.alvoDeToqueMinimo
import br.com.memoriaplay.accessibility.avisoAoVivo
import br.com.memoriaplay.accessibility.tituloAcessivel
import br.com.memoriaplay.features.config.model.EscalaTexto
import br.com.memoriaplay.features.config.viewmodel.ConfiguracoesViewModel

/** Tela de Configurações: só mostra o estado e avisa o ViewModel dos toques. */
@Composable
fun ConfiguracoesScreen(
    viewModel: ConfiguracoesViewModel,
    aoSair: (() -> Unit)? = null
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val preferencias = estado.preferencias

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Configurações",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.tituloAcessivel()
        )
        Text(
            "Ajuste o aplicativo para ver e usar melhor. As escolhas ficam salvas neste aparelho.",
            style = MaterialTheme.typography.bodyLarge
        )

        estado.mensagemErro?.let { mensagem ->
            AvisoDeErro(mensagem = mensagem, aoFechar = viewModel::onLimparErro)
        }

        Text(
            "Visão",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.tituloAcessivel()
        )
        OpcaoComCaixa(
            titulo = "Alto contraste",
            descricao = "Cores fortes: texto preto sobre fundo branco.",
            marcada = preferencias.altoContraste,
            aoAlterar = viewModel::onAltoContrasteChange
        )

        Text(
            "Tamanho do texto",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.tituloAcessivel()
        )
        EscalaTexto.entries.forEach { escala ->
            OpcaoComCaixa(
                titulo = escala.rotulo,
                descricao = null,
                marcada = preferencias.escalaTexto == escala,
                aoAlterar = { viewModel.onEscalaTextoChange(escala) }
            )
        }
        Text(
            "Exemplo: é assim que as letras aparecem no aplicativo.",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            "Avisos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.tituloAcessivel()
        )
        OpcaoComCaixa(
            titulo = "Avisos falados do jogo",
            descricao = "Anuncia par encontrado, erro e vitória para quem usa leitor de tela.",
            marcada = preferencias.feedbackAcessivel,
            aoAlterar = viewModel::onFeedbackAcessivelChange
        )

        if (aoSair != null) {
            Button(onClick = aoSair, modifier = Modifier.fillMaxWidth()) {
                Text("Sair / trocar jogador")
            }
        }
    }
}

/** Linha inteira clicável com uma caixa de marcação. */
@Composable
private fun OpcaoComCaixa(
    titulo: String,
    descricao: String?,
    marcada: Boolean,
    aoAlterar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alvoDeToqueMinimo()
            .toggleable(
                value = marcada,
                role = Role.Checkbox,
                onValueChange = aoAlterar
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            if (descricao != null) {
                Text(descricao, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Checkbox(checked = marcada, onCheckedChange = null)
    }
}

/** Mensagem de erro com botão para fechar. */
@Composable
private fun AvisoDeErro(
    mensagem: String,
    aoFechar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                mensagem,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.avisoAoVivo(urgente = true)
            )
            Button(onClick = aoFechar) { Text("Entendi") }
        }
    }
}
