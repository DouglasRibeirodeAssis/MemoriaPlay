package br.com.memoriaplay.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.data.remote.FirebaseDesafiosSource
import br.com.memoriaplay.features.admin.data.AdminRepository
import br.com.memoriaplay.features.admin.data.SessaoRepository
import br.com.memoriaplay.features.admin.ui.AdminScreen
import br.com.memoriaplay.features.admin.ui.LoginScreen
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModelFactory
import br.com.memoriaplay.di.AppViewModelFactory
import br.com.memoriaplay.features.catalogo.data.CatalogoRepository
import br.com.memoriaplay.features.catalogo.ui.CatalogoScreen
import br.com.memoriaplay.features.catalogo.ui.DetalhesTemaScreen
import br.com.memoriaplay.features.catalogo.viewmodel.DetalhesTemaViewModel
import br.com.memoriaplay.features.catalogo.viewmodel.CatalogoViewModel
import br.com.memoriaplay.features.desafios.data.DesafiosRepository
import br.com.memoriaplay.features.desafios.ui.DesafiosScreen
import br.com.memoriaplay.features.desafios.viewmodel.DesafiosViewModel
import br.com.memoriaplay.features.desafios.model.Desafio
import br.com.memoriaplay.features.home.ui.HomeScreen
import br.com.memoriaplay.features.home.viewmodel.HomeViewModel
import br.com.memoriaplay.features.jogo.data.JogoRepository
import br.com.memoriaplay.features.jogo.data.RoomJogoDataSource
import br.com.memoriaplay.features.jogo.model.ConfiguracaoPartida
import br.com.memoriaplay.features.jogo.model.NivelJogo
import br.com.memoriaplay.features.jogo.ui.JogoScreen
import br.com.memoriaplay.features.jogo.ui.ResultadoScreen
import br.com.memoriaplay.features.jogo.ui.SelecionarModoScreen
import br.com.memoriaplay.features.jogo.viewmodel.JogoViewModel
import br.com.memoriaplay.features.ranking.ui.RankingScreen
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@Composable
fun NavegacaoApp(viewModelFactory: AppViewModelFactory) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route
    val mostrarAbas = rotaAtual in Rotas.destinosPrincipais
    val contexto = LocalContext.current.applicationContext
    val banco = remember(contexto) { AppDatabase.obter(contexto) }
    val sessao = remember(banco) { SessaoRepository(banco.usuarioDao()) }
    val fabricaAdmin = remember(banco, sessao) {
        AdminViewModelFactory(
            AdminRepository(
                banco.temaDao(), banco.partidaDao(), banco.categoriaDao(), banco.cartaDao()
            ), sessao
        )
    }
    val adminViewModel: AdminViewModel = viewModel(factory = fabricaAdmin)
    val perfil by sessao.perfilLogado.collectAsState()

    val fabricaJogo = remember(contexto) {
        val banco = AppDatabase.obter(contexto)
        val fonte = RoomJogoDataSource(
            banco.temaDao(),
            banco.partidaDao(),
            banco.cartaDao()
        )
        JogoViewModel.fabrica(JogoRepository(fonte))
    }

    Scaffold(
        bottomBar = {
            if (mostrarAbas) {
                BarraNavegacao(rotaAtual = rotaAtual) { rota ->
                    if (rota == Rotas.INICIO) {
                        // Retorna à Home existente, sem restaurar telas de outras abas.
                        if (rotaAtual != Rotas.INICIO) {
                            val voltouAoInicio = navController.popBackStack(
                                Rotas.INICIO, inclusive = false
                            )
                            if (!voltouAoInicio) {
                                // Recupera a Home mesmo se ela não estiver mais na pilha.
                                navController.navigate(Rotas.INICIO) {
                                    popUpTo(navController.graph.id) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                    } else {
                        navController.navigate(rota) {
                            popUpTo(Rotas.INICIO) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.LOGIN) {
                LoginScreen(
                    viewModel = adminViewModel,
                    aoEntrarComoAdmin = {
                        navController.navigate(Rotas.ADMIN) {
                            popUpTo(Rotas.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    aoEntrarComoJogador = {
                        navController.navigate(Rotas.INICIO) {
                            popUpTo(Rotas.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Rotas.INICIO) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = viewModelFactory
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    aoIniciarPartida = {
                        navController.navigate(Rotas.selecionarModo())
                    },
                    aoAbrirCatalogo = {
                        navController.navigate(Rotas.CATALOGO)
                    }
                )
            }

            composable(
                route = Rotas.SELECIONAR_MODO_COM_TEMA,
                arguments = listOf(
                    navArgument(Rotas.ARG_TEMA) {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                    navArgument(Rotas.ARG_DESAFIO) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { entry ->
                val id = entry.arguments
                    ?.getLong(Rotas.ARG_TEMA)
                    ?.takeIf { it > 0L }
                val desafioId = entry.arguments?.getString(Rotas.ARG_DESAFIO)
                    ?.takeIf { it.isNotBlank() }
                if (desafioId != null) {
                    val desafio = remember(entry) { entry.recuperarDesafio() }
                    val jaIniciado = entry.savedStateHandle.get<Boolean>(CH_INICIADO) == true
                    LaunchedEffect(entry.id, desafio?.id) {
                        if (!jaIniciado && desafio != null && desafio.id == desafioId &&
                            desafio.quantidadePares in 3..12
                        ) {
                            entry.savedStateHandle[CH_INICIADO] = true
                            val modo = when (desafio.quantidadePares) {
                                3 -> NivelJogo.FACIL
                                8 -> NivelJogo.MEDIO
                                12 -> NivelJogo.DIFICIL
                                else -> NivelJogo.PERSONALIZADO
                            }
                            navController.navigate(
                                Rotas.jogo(modo.name, desafio.quantidadePares)
                            ) { launchSingleTop = true }
                        }
                    }
                    TelaDestino(
                        titulo = when {
                            jaIniciado -> "Desafio interrompido"
                            desafio != null -> "Preparando desafio..."
                            else -> "Não foi possível recuperar o desafio"
                        },
                        descricao = if (jaIniciado || desafio == null)
                            "Volte à lista de desafios para iniciar uma nova missão."
                            else desafio.titulo,
                        acao = if (jaIniciado || desafio == null) {
                            { navController.popBackStack(); Unit }
                        } else null,
                        textoAcao = "Voltar aos desafios"
                    )
                } else {
                    SelecionarModoScreen(
                        aoSelecionarNivel = { modo, pares ->
                            navController.navigate(Rotas.jogo(modo, pares, id))
                        }
                    )
                }
            }

            composable(
                route = Rotas.JOGO,
                arguments = listOf(
                    navArgument(Rotas.ARG_MODO) {
                        type = NavType.StringType
                    },
                    navArgument(Rotas.ARG_PARES) {
                        type = NavType.IntType
                        defaultValue = 3
                    },
                    navArgument(Rotas.ARG_TEMA) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { entry ->
                val selecaoEntry = remember(entry) {
                    navController.getBackStackEntry(
                        Rotas.SELECIONAR_MODO_COM_TEMA
                    )
                }
                val vm: JogoViewModel = viewModel(
                    viewModelStoreOwner = selecaoEntry,
                    factory = fabricaJogo
                )
                val modo = NivelJogo.daRota(
                    entry.arguments?.getString(Rotas.ARG_MODO).orEmpty()
                )
                val pares = modo.paresFixos ?: entry.arguments
                    ?.getInt(Rotas.ARG_PARES)
                    ?.coerceIn(3, 12) ?: 3
                val id = entry.arguments
                    ?.getLong(Rotas.ARG_TEMA)
                    ?.takeIf { it > 0L }
                val desafio = remember(selecaoEntry) {
                    if (selecaoEntry.arguments?.getString(Rotas.ARG_DESAFIO)
                            .isNullOrBlank()) null
                    else selecaoEntry.recuperarDesafio()
                }

                LaunchedEffect(entry.id) {
                    vm.iniciarSeNecessario(
                        entry.id,
                        ConfiguracaoPartida(
                            nivel = modo,
                            quantidadePares = pares,
                            temaId = id,
                            jogador = perfil?.nome ?: "Jogador",
                            desafio = desafio
                        )
                    )
                }

                JogoScreen(
                    viewModel = vm,
                    aoVoltar = {
                        navController.popBackStack()
                    },
                    aoConcluir = {
                        navController.navigate(Rotas.RESULTADO) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Rotas.RESULTADO) { entry ->
                val selecaoEntry = remember(entry) {
                    navController.getBackStackEntry(
                        Rotas.SELECIONAR_MODO_COM_TEMA
                    )
                }
                val vm: JogoViewModel = viewModel(
                    viewModelStoreOwner = selecaoEntry,
                    factory = fabricaJogo
                )
                val configuracao = vm.uiState.value.configuracao

                ResultadoScreen(
                    viewModel = vm,
                    aoJogarNovamente = {
                        configuracao?.let {
                            vm.iniciarPartida(it)
                            navController.navigate(
                                Rotas.jogo(
                                    it.nivel.name,
                                    it.quantidadePares,
                                    it.temaId
                                )
                            ) {
                                popUpTo(Rotas.JOGO) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    aoVerRanking = {
                        navController.navigate(Rotas.RANKING) {
                            popUpTo(Rotas.INICIO)
                            launchSingleTop = true
                        }
                    },
                    aoVoltar = {
                        val destino = if (configuracao?.desafio != null) Rotas.DESAFIOS
                            else Rotas.INICIO
                        navController.navigate(destino) {
                            popUpTo(Rotas.INICIO)
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Rotas.CATALOGO) {
                val catalogoViewModel: CatalogoViewModel = viewModel(
                    factory = CatalogoViewModel.fabrica(
                        CatalogoRepository(
                            banco.temaDao(), banco.categoriaDao(), banco.cartaDao()
                        )
                    )
                )
                CatalogoScreen(
                    viewModel = catalogoViewModel,
                    onSelecionarTema = { id ->
                        navController.navigate(Rotas.selecionarModo(id))
                    },
                    onVerCartas = { id -> navController.navigate(Rotas.detalhesTema(id)) }
                )
            }

            composable(
                Rotas.DETALHES_TEMA,
                arguments = listOf(navArgument(Rotas.ARG_TEMA) { type = NavType.LongType })
            ) { entry ->
                val temaId = entry.arguments?.getLong(Rotas.ARG_TEMA) ?: -1L
                val detalhes: DetalhesTemaViewModel = viewModel(
                    factory = DetalhesTemaViewModel.fabrica(
                        CatalogoRepository(banco.temaDao(), banco.categoriaDao(), banco.cartaDao()),
                        temaId
                    )
                )
                DetalhesTemaScreen(
                    viewModel = detalhes,
                    aoJogar = { navController.navigate(Rotas.selecionarModo(temaId)) },
                    aoVoltar = { navController.popBackStack() }
                )
            }

            composable(Rotas.RANKING) {
                RankingScreen()
            }

            composable(Rotas.DESAFIOS) {
                val desafiosViewModel: DesafiosViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            DesafiosViewModel(
                                DesafiosRepository(
                                    FirebaseDesafiosSource()
                                )
                            )
                        }
                    }
                )
                DesafiosScreen(
                    viewModel = desafiosViewModel,
                    aoIniciarDesafio = { desafio ->
                        navController.navigate(Rotas.selecionarDesafio(desafio.id))
                        navController.getBackStackEntry(
                            Rotas.SELECIONAR_MODO_COM_TEMA
                        ).salvarDesafio(desafio)
                    }
                )
            }

            composable(Rotas.CONFIGURACOES) {
                TelaDestino(
                    titulo = "Configurações",
                    descricao = "Preferências e acessibilidade serão integradas pelo " +
                        "integrante responsável. Você entrou como ${perfil?.nome ?: "Jogador"}.",
                    acao = {
                        adminViewModel.onSair()
                        navController.navigate(Rotas.LOGIN) {
                            popUpTo(Rotas.INICIO) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    textoAcao = "Sair / trocar jogador"
                )
            }

            composable(Rotas.ADMIN) {
                AdminScreen(
                    viewModel = adminViewModel,
                    aoSair = {
                        adminViewModel.onSair()
                        navController.navigate(Rotas.LOGIN) {
                            popUpTo(Rotas.ADMIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TelaDestino(
    titulo: String,
    descricao: String,
    acao: (() -> Unit)? = null,
    textoAcao: String = "Continuar"
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo)
        Text(descricao)
        if (acao != null) {
            Button(onClick = acao) { Text(textoAcao) }
        }
    }
}


private const val CH_ID = "desafio_snapshot_id"
private const val CH_TITULO = "desafio_snapshot_titulo"
private const val CH_DESCRICAO = "desafio_snapshot_descricao"
private const val CH_PARES = "desafio_snapshot_pares"
private const val CH_TEMPO = "desafio_snapshot_tempo"
private const val CH_TENTATIVAS = "desafio_snapshot_tentativas"
private const val CH_INICIADO = "desafio_snapshot_iniciado"

private fun NavBackStackEntry.salvarDesafio(desafio: Desafio) {
    savedStateHandle[CH_ID] = desafio.id
    savedStateHandle[CH_TITULO] = desafio.titulo
    savedStateHandle[CH_DESCRICAO] = desafio.descricao
    savedStateHandle[CH_PARES] = desafio.quantidadePares
    savedStateHandle[CH_TEMPO] = desafio.tempoLimiteSegundos ?: -1
    savedStateHandle[CH_TENTATIVAS] = desafio.tentativasMaximas ?: -1
    savedStateHandle[CH_INICIADO] = false
}

private fun NavBackStackEntry.recuperarDesafio(): Desafio? {
    val id = savedStateHandle.get<String>(CH_ID)?.takeIf { it.isNotBlank() }
        ?: return null
    val pares = savedStateHandle.get<Int>(CH_PARES) ?: return null
    return Desafio(
        id = id,
        titulo = savedStateHandle.get<String>(CH_TITULO).orEmpty(),
        descricao = savedStateHandle.get<String>(CH_DESCRICAO).orEmpty(),
        quantidadePares = pares,
        tempoLimiteSegundos = savedStateHandle.get<Int>(CH_TEMPO)
            ?.takeIf { it > 0 },
        tentativasMaximas = savedStateHandle.get<Int>(CH_TENTATIVAS)
            ?.takeIf { it > 0 }
    )
}
