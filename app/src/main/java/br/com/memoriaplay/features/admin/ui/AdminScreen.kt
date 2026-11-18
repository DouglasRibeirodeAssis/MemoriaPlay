package br.com.memoriaplay.features.admin.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    aoSair: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administração") },
                actions = {
                    TextButton(onClick = {
                        viewModel.onSair()
                        aoSair()
                    }) {
                        Text("Sair", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        if (!uiState.ehAdmin) {
            AcessoRestrito(
                modifier = Modifier.padding(padding),
                aoVoltar = aoSair
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                MensagemStatus(
                    sucesso = uiState.mensagemSucesso,
                    erro = uiState.mensagemErro,
                    aoFechar = { viewModel.onLimparMensagem() }
                )
            }

            item {
                Text("Temas", style = MaterialTheme.typography.titleLarge)
            }

            item {
                OutlinedTextField(
                    value = uiState.nomeTema,
                    onValueChange = { viewModel.onNomeTemaChange(it) },
                    label = { Text("Nome do tema") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.ordemTema,
                        onValueChange = { viewModel.onOrdemTemaChange(it) },
                        label = { Text("Ordem") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp)
                    )
                    Switch(
                        checked = uiState.ativoTema,
                        onCheckedChange = { viewModel.onAtivoTemaChange(it) }
                    )
                    Text(if (uiState.ativoTema) "Tema ativo" else "Tema inativo")
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.onSalvarTema() }) {
                        if (uiState.temaEmEdicao != null) Text("Atualizar tema")
                        else Text("Adicionar tema")
                    }
                    if (uiState.temaEmEdicao != null) {
                        OutlinedButton(onClick = { viewModel.onCancelarEdicaoTema() }) {
                            Text("Cancelar")
                        }
                    }
                }
            }

            if (uiState.temas.isEmpty()) {
                item { Text("Nenhum tema cadastrado.") }
            }

            items(uiState.temas) { tema ->
                UmTema(
                    tema = tema,
                    aoEditar = { viewModel.onEditarTema(it) },
                    aoExcluir = { viewModel.onExcluirTema(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Partidas", style = MaterialTheme.typography.titleLarge)
            }

            uiState.partidaEmEdicao?.let { partida ->
                item {
                    EditarApelido(
                        partida = partida,
                        apelido = uiState.apelidoPartida,
                        aoMudarApelido = { viewModel.onApelidoPartidaChange(it) },
                        aoSalvar = { viewModel.onSalvarApelido() },
                        aoCancelar = { viewModel.onCancelarEdicaoPartida() }
                    )
                }
            }

            if (uiState.partidas.isEmpty()) {
                item { Text("Nenhuma partida registrada.") }
            }

            items(uiState.partidas) { partida ->
                UmaPartida(
                    partida = partida,
                    aoEditar = { viewModel.onEditarPartida(it) },
                    aoExcluir = { viewModel.onExcluirPartida(it) }
                )
            }
        }
    }
}

@Composable
fun UmTema(tema: TemaEntity, aoEditar: (TemaEntity) -> Unit, aoExcluir: (TemaEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tema.nome, style = MaterialTheme.typography.titleMedium)
                Text("Ordem ${tema.ordem} · ${if (tema.ativo) "Ativo" else "Inativo"}")
            }
            TextButton(onClick = { aoEditar(tema) }) { Text("Editar") }
            TextButton(onClick = { aoExcluir(tema) }) { Text("Excluir") }
        }
    }
}

@Composable
fun UmaPartida(partida: PartidaEntity, aoEditar: (PartidaEntity) -> Unit, aoExcluir: (PartidaEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(partida.jogador, style = MaterialTheme.typography.titleMedium)
                Text("${partida.nomeTema} · ${partida.modo}")
                Text("${partida.tempoSegundos} s · ${partida.tentativas} tentativas")
            }
            TextButton(onClick = { aoEditar(partida) }) { Text("Editar apelido") }
            TextButton(onClick = { aoExcluir(partida) }) { Text("Excluir") }
        }
    }
}

@Composable
fun EditarApelido(
    partida: PartidaEntity,
    apelido: String,
    aoMudarApelido: (String) -> Unit,
    aoSalvar: () -> Unit,
    aoCancelar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Editando a partida nº ${partida.id}")
            OutlinedTextField(
                value = apelido,
                onValueChange = aoMudarApelido,
                label = { Text("Novo apelido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = aoSalvar) { Text("Salvar apelido") }
                OutlinedButton(onClick = aoCancelar) { Text("Cancelar") }
            }
        }
    }
}

@Composable
fun MensagemStatus(sucesso: String?, erro: String?, aoFechar: () -> Unit) {
    val texto = erro ?: sucesso ?: return
    val corTexto = if (erro != null) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.primary

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(texto, color = corTexto, modifier = Modifier.weight(1f))
            TextButton(onClick = aoFechar) { Text("OK") }
        }
    }
}

@Composable
fun AcessoRestrito(modifier: Modifier = Modifier, aoVoltar: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Acesso restrito", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Esta área é só para o perfil Administrador.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = aoVoltar) { Text("Voltar") }
    }
}