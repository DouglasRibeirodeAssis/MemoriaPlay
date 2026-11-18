package br.com.memoriaplay.features.admin.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.memoriaplay.data.local.entity.CartaEntity
import br.com.memoriaplay.data.local.entity.CategoriaEntity
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.features.catalogo.data.RecursosCartas
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel

// Paleta usada também na nova LoginScreen do Memória Play.
private val ROXO = Color(0xFF673AB7)
private val ROXO_ESCURO = Color(0xFF4B297B)
private val ROSA_CLARO = Color(0xFFFFE5F3)
private val AMARELO_CLARO = Color(0xFFFFF3D8)
private val LILAS_CLARO = Color(0xFFE7E5FF)
private val FUNDO_ADMIN = Brush.verticalGradient(
    listOf(AMARELO_CLARO, ROSA_CLARO, LILAS_CLARO)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    aoSair: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = ROXO,
            onPrimary = Color.White,
            primaryContainer = ROSA_CLARO,
            onPrimaryContainer = ROXO_ESCURO,
            secondary = ROXO_ESCURO,
            onSecondary = Color.White,
            surface = Color.White,
            onSurface = ROXO_ESCURO,
            background = AMARELO_CLARO,
            onBackground = ROXO_ESCURO
        )
    ) {
        Scaffold(
        modifier = Modifier.background(FUNDO_ADMIN),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("🎮 Administração", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = {
                        viewModel.onSair()
                        aoSair()
                    }) {
                        Text("Sair", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ROXO,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ROSA_CLARO)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🧩 Painel do administrador",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = ROXO_ESCURO)
                        Text("Organize temas, categorias, cartas e partidas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ROXO_ESCURO)
                    }
                }
            }

            item {
                MensagemStatus(
                    sucesso = uiState.mensagemSucesso,
                    erro = uiState.mensagemErro,
                    aoFechar = { viewModel.onLimparMensagem() }
                )
            }

            item {
                TituloSecao("🎨 Temas", "Crie e organize os temas do jogo.")
                Text(
                    "Para um novo tema, cadastre categorias e pelo menos 3 cartas " +
                        "com imagens diferentes para começar a jogar.",
                    style = MaterialTheme.typography.bodySmall
                )
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

            // CRUD de categorias e cartas do mesmo banco Room do Catálogo.
            item {
                Spacer(Modifier.height(16.dp))
                TituloSecao("🗂️ Categorias", "Organize as cartas dentro de cada tema.")
                Text("Cada categoria pertence a um tema. Excluir uma categoria remove suas cartas.")
                SeletorItem(
                    titulo = "Tema das categorias",
                    selecionado = uiState.temas.firstOrNull { it.id == uiState.temaCategoriaId }?.nome
                        ?: "Selecione um tema",
                    opcoes = uiState.temas.map { it.id to it.nome },
                    aoSelecionar = viewModel::onSelecionarTemaCategoria
                )
                OutlinedTextField(
                    value = uiState.nomeCategoria,
                    onValueChange = viewModel::onNomeCategoriaChange,
                    label = { Text("Nome da categoria") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = uiState.ordemCategoria,
                        onValueChange = viewModel::onOrdemCategoriaChange,
                        label = { Text("Ordem") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp)
                    )
                    Switch(
                        checked = uiState.ativoCategoria,
                        onCheckedChange = viewModel::onAtivoCategoriaChange
                    )
                    Text("Ativa")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = viewModel::onSalvarCategoria) {
                        Text(if (uiState.categoriaEmEdicao == null) "Adicionar categoria" else "Atualizar categoria")
                    }
                    if (uiState.categoriaEmEdicao != null) {
                        OutlinedButton(onClick = viewModel::onCancelarEdicaoCategoria) {
                            Text("Cancelar")
                        }
                    }
                }
            }
            val categoriasDoTema = uiState.categorias.filter { it.temaId == uiState.temaCategoriaId }
            if (categoriasDoTema.isEmpty()) item { Text("Nenhuma categoria neste tema.") }
            items(categoriasDoTema, key = { "cat_${it.id}" }) { categoria ->
                CategoriaAdminItem(
                    categoria = categoria,
                    aoEditar = { viewModel.onEditarCategoria(it) },
                    aoExcluir = { viewModel.onExcluirCategoria(it) }
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                TituloSecao("🃏 Cartas", "Escolha imagens e organize cada carta.")
                Text("Escolha uma categoria e uma imagem existente no aplicativo.")
                SeletorItem(
                    titulo = "Categoria das cartas",
                    selecionado = uiState.categorias.firstOrNull { it.id == uiState.categoriaCartaId }?.nome
                        ?: "Selecione a categoria",
                    opcoes = categoriasDoTema.map { it.id to it.nome },
                    aoSelecionar = viewModel::onSelecionarCategoriaCarta
                )
                OutlinedTextField(
                    value = uiState.nomeCarta,
                    onValueChange = viewModel::onNomeCartaChange,
                    label = { Text("Nome da carta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                SeletorRecursoCarta(
                    selecionado = uiState.recursoCarta,
                    aoSelecionar = viewModel::onRecursoCartaChange
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = uiState.ordemCarta,
                        onValueChange = viewModel::onOrdemCartaChange,
                        label = { Text("Ordem") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp)
                    )
                    Switch(
                        checked = uiState.ativoCarta,
                        onCheckedChange = viewModel::onAtivoCartaChange
                    )
                    Text("Ativa")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = viewModel::onSalvarCarta) {
                        Text(if (uiState.cartaEmEdicao == null) "Adicionar carta" else "Atualizar carta")
                    }
                    if (uiState.cartaEmEdicao != null) {
                        OutlinedButton(onClick = viewModel::onCancelarEdicaoCarta) {
                            Text("Cancelar")
                        }
                    }
                }
            }
            val cartasDaCategoria = uiState.cartas.filter { it.categoriaId == uiState.categoriaCartaId }
            if (cartasDaCategoria.isEmpty()) item { Text("Nenhuma carta nesta categoria.") }
            items(cartasDaCategoria, key = { "carta_${it.id}" }) { carta ->
                CartaAdminItem(
                    carta = carta,
                    aoEditar = { viewModel.onEditarCarta(it) },
                    aoExcluir = { viewModel.onExcluirCarta(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                TituloSecao("🏆 Partidas", "Consulte e gerencie o histórico de partidas.")
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
}

@Composable
private fun TituloSecao(titulo: String, descricao: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = ROXO_ESCURO)
            Text(descricao, style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF655473))
        }
    }
}

@Composable
fun UmTema(tema: TemaEntity, aoEditar: (TemaEntity) -> Unit, aoExcluir: (TemaEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFDECDF5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tema.nome, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = ROXO_ESCURO)
                Text("Ordem ${tema.ordem} · ${if (tema.ativo) "Ativo" else "Inativo"}")
            }
            TextButton(onClick = { aoEditar(tema) }) { Text("Editar") }
            TextButton(onClick = { aoExcluir(tema) }) { Text("Excluir", color = Color(0xFFB3261E)) }
        }
    }
}

@Composable
fun UmaPartida(partida: PartidaEntity, aoEditar: (PartidaEntity) -> Unit, aoExcluir: (PartidaEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFDECDF5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(partida.jogador, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = ROXO_ESCURO)
                Text("${partida.nomeTema} · ${partida.modo}")
                Text("${partida.tempoSegundos} s · ${partida.tentativas} tentativas")
            }
            TextButton(onClick = { aoEditar(partida) }) { Text("Editar apelido") }
            TextButton(onClick = { aoExcluir(partida) }) { Text("Excluir", color = Color(0xFFB3261E)) }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Editando a partida nº ${partida.id}", fontWeight = FontWeight.Bold)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (erro != null) Color(0xFFFFE4E4) else Color(0xFFE8F5E9)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(texto, color = if (erro != null) Color(0xFF9D2020) else Color(0xFF245C2A),
                modifier = Modifier.weight(1f))
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
        Text("🔒 Acesso restrito", style = MaterialTheme.typography.headlineSmall,
            color = ROXO_ESCURO, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Esta área é só para o perfil Administrador.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = aoVoltar) { Text("Voltar") }
    }
}

/** Seletor por botão, sem dependências de bibliotecas adicionais. */
@Composable
private fun SeletorItem(
    titulo: String,
    selecionado: String,
    opcoes: List<Pair<Long, String>>,
    aoSelecionar: (Long) -> Unit
) {
    var aberto by remember { mutableStateOf(false) }
    Column {
        Text(titulo, style = MaterialTheme.typography.labelLarge)
        OutlinedButton(onClick = { aberto = true }, enabled = opcoes.isNotEmpty()) {
            Text(selecionado)
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEach { (id, nome) ->
                DropdownMenuItem(
                    text = { Text(nome) },
                    onClick = { aberto = false; aoSelecionar(id) }
                )
            }
        }
    }
}

@Composable
private fun SeletorRecursoCarta(
    selecionado: String,
    aoSelecionar: (String) -> Unit
) {
    var aberto by remember { mutableStateOf(false) }
    val recurso = RecursosCartas.buscar(selecionado)
    Column {
        Text("Imagem da carta", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (recurso != null) {
                Image(
                    painter = painterResource(id = recurso.drawable),
                    contentDescription = recurso.nome,
                    modifier = Modifier.size(44.dp)
                )
            }
            OutlinedButton(onClick = { aberto = true }) {
                Text(recurso?.nome ?: "Selecionar imagem")
            }
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            RecursosCartas.opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(opcao.nome + " (" + opcao.chave + ")") },
                    onClick = { aberto = false; aoSelecionar(opcao.chave) }
                )
            }
        }
    }
}

@Composable
private fun CategoriaAdminItem(
    categoria: CategoriaEntity,
    aoEditar: (CategoriaEntity) -> Unit,
    aoExcluir: (CategoriaEntity) -> Unit
) {
    var confirmarExclusao by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(categoria.nome, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = ROXO_ESCURO)
                Text(if (categoria.ativo) "Ativa" else "Inativa")
            }
            TextButton(onClick = { aoEditar(categoria) }) { Text("Editar") }
            TextButton(onClick = { confirmarExclusao = true }) { Text("Excluir", color = Color(0xFFB3261E)) }
        }
    }
    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir categoria?") },
            text = { Text("Todas as cartas desta categoria também serão excluídas.") },
            confirmButton = {
                TextButton(onClick = { confirmarExclusao = false; aoExcluir(categoria) }) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CartaAdminItem(
    carta: CartaEntity,
    aoEditar: (CartaEntity) -> Unit,
    aoExcluir: (CartaEntity) -> Unit
) {
    val recurso = RecursosCartas.buscar(carta.imagemRecurso)
    var confirmarExclusao by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (recurso != null) {
                Image(
                    painter = painterResource(id = recurso.drawable),
                    contentDescription = carta.nome,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(carta.nome, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = ROXO_ESCURO)
                Text(if (carta.ativo) "Ativa" else "Inativa")
            }
            TextButton(onClick = { aoEditar(carta) }) { Text("Editar") }
            TextButton(onClick = { confirmarExclusao = true }) { Text("Excluir", color = Color(0xFFB3261E)) }
        }
    }
    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir carta?") },
            text = { Text("A carta será removida desta categoria.") },
            confirmButton = {
                TextButton(onClick = { confirmarExclusao = false; aoExcluir(carta) }) { Text("Excluir", color = Color(0xFFB3261E)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}
