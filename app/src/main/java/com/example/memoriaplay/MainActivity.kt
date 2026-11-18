package com.example.memoriaplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.memoriaplay.data.local.AppDatabase
import br.com.memoriaplay.features.admin.data.AdminRepository
import br.com.memoriaplay.features.admin.data.SessaoRepository
import br.com.memoriaplay.features.admin.ui.AdminScreen
import br.com.memoriaplay.features.admin.ui.LoginScreen
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModelFactory
import br.com.memoriaplay.features.catalogo.data.CatalogoRepository
import br.com.memoriaplay.features.catalogo.ui.CatalogoScreen
import br.com.memoriaplay.features.catalogo.viewmodel.CatalogoViewModel
import com.example.memoriaplay.ui.theme.MemoriaPlayTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var sessaoRepository: SessaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.obter(applicationContext)
        val adminRepository = AdminRepository(database.temaDao(), database.partidaDao())
        val catalogoRepository = CatalogoRepository(database.temaDao())
        sessaoRepository = SessaoRepository(database.usuarioDao())

        setContent {
            MemoriaPlayTheme {
                var telaAtual by remember { mutableStateOf("login") }

                val adminViewModel: AdminViewModel = viewModel(
                    factory = AdminViewModelFactory(adminRepository, sessaoRepository)
                )

                val catalogoViewModel: CatalogoViewModel = viewModel(
                    factory = CatalogoViewModel.fabrica(catalogoRepository)
                )

                when (telaAtual) {
                    "login" -> {
                        LoginScreen(
                            viewModel = adminViewModel,
                            aoEntrarComoAdmin = { telaAtual = "admin" },
                            aoEntrarComoJogador = { telaAtual = "catalogo" }
                        )
                    }
                    "admin" -> {
                        AdminScreen(
                            viewModel = adminViewModel,
                            aoSair = {
                                adminViewModel.onSair()
                                telaAtual = "login"
                            }
                        )
                    }
                    "catalogo" -> {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text("Memória Play — Jogador") },
                                    actions = {
                                        TextButton(onClick = {
                                            adminViewModel.onSair()
                                            telaAtual = "login"
                                        }) {
                                            Text("Sair", color = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        ) { padding ->
                            Box(modifier = Modifier.padding(padding)) {
                                CatalogoScreen(
                                    viewModel = catalogoViewModel,
                                    onSelecionarTema = { _ ->
                                        // Tema selecionado para iniciar o jogo
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
