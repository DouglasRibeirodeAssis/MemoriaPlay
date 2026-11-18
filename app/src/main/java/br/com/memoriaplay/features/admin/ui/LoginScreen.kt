package br.com.memoriaplay.features.admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AdminViewModel,
    aoEntrarComoAdmin: () -> Unit,
    aoEntrarComoJogador: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.perfilLogado) {
        val perfil = uiState.perfilLogado ?: return@LaunchedEffect
        if (perfil.ehAdmin) aoEntrarComoAdmin() else aoEntrarComoJogador()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Memória Play — Entrar") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Escolha seu perfil",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = uiState.usuario,
                onValueChange = { viewModel.onUsuarioChange(it) },
                label = { Text("Usuário") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.senha,
                onValueChange = { viewModel.onSenhaChange(it) },
                label = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            uiState.mensagemErro?.let { erro ->
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.perfilLogado?.let { perfil ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = uiState.mensagemSucesso ?: "Logado como ${perfil.nome}",
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Perfil: ${perfil.descricaoTipo}")
                        TextButton(onClick = { viewModel.onSair() }) {
                            Text("Sair")
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.onEntrar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Usuários de demonstração", style = MaterialTheme.typography.titleSmall)
                    Text("admin / 1234 → Administrador")
                    Text("jogador / 1234 → Jogador")
                }
            }
        }
    }
}