package br.com.memoriaplay.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.memoriaplay.di.AppViewModelFactory
import br.com.memoriaplay.features.home.ui.HomeScreen
import br.com.memoriaplay.features.home.viewmodel.HomeViewModel
import br.com.memoriaplay.features.jogo.ui.SelecionarModoScreen

@Composable
fun NavegacaoApp(viewModelFactory: AppViewModelFactory) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route
    val mostrarAbas = rotaAtual in Rotas.destinosPrincipais

    Scaffold(
        bottomBar = {
            if (mostrarAbas) {
                BarraNavegacao(rotaAtual = rotaAtual) { rota ->
                    navController.navigate(rota) {
                        popUpTo(Rotas.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.LOGIN) {
                TelaDestino("Entrar", "Acesso demonstrativo") {
                    navController.navigate(Rotas.INICIO) {
                        popUpTo(Rotas.LOGIN) { inclusive = true }
                    }
                }
            }
            composable(Rotas.INICIO) {
                val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                HomeScreen(
                    viewModel = homeViewModel,
                    aoIniciarPartida = { navController.navigate(Rotas.SELECIONAR_MODO) },
                    aoAbrirCatalogo = { navController.navigate(Rotas.CATALOGO) }
                )
            }
            composable(Rotas.SELECIONAR_MODO) {
                SelecionarModoScreen(
                    aoSelecionarNivel = { nivel ->
                        navController.navigate(Rotas.jogo(nivel))
                    }
                )
            }
            composable(
                route = Rotas.JOGO,
                arguments = listOf(navArgument(Rotas.ARG_MODO) { type = NavType.StringType })
            ) { entry ->
                val modo = entry.arguments?.getString(Rotas.ARG_MODO).orEmpty()
                TelaDestino("Partida", "Modo selecionado: $modo") {
                    navController.navigate(Rotas.RESULTADO)
                }
            }
            composable(Rotas.RESULTADO) {
                TelaDestino("Resultado", "Resumo da partida concluída.") {
                    navController.navigate(Rotas.INICIO) {
                        popUpTo(Rotas.INICIO) { inclusive = false }
                    }
                }
            }
            composable(Rotas.CATALOGO) {
                TelaDestino("Catálogo", "Temas disponíveis para jogar.")
            }
            composable(Rotas.RANKING) {
                TelaDestino("Ranking", "Suas melhores partidas.")
            }
            composable(Rotas.DESAFIOS) {
                TelaDestino("Desafios", "Desafios disponíveis.")
            }
            composable(Rotas.CONFIGURACOES) {
                TelaDestino("Configurações", "Preferências e acessibilidade.")
            }
            composable(Rotas.ADMIN) {
                TelaDestino("Administração", "Área administrativa demonstrativa.")
            }
        }
    }
}

@Composable
private fun TelaDestino(
    titulo: String,
    descricao: String,
    acao: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo)
        Text(descricao)
        if (acao != null) {
            Button(onClick = acao) { Text("Continuar") }
        }
    }
}
