package br.com.memoriaplay.features.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.memoriaplay.features.admin.viewmodel.AdminViewModel

// Tema visual aprovado, sem alterar autenticação nem permissões.
private val ROXO = Color(0xFF673AB7)
private val ROXO_ESCURO = Color(0xFF4B297B)
private val ROSA = Color(0xFFFF91C2)
private val AMARELO = Color(0xFFFFCC70)
private val FUNDO_LOGIN = Brush.verticalGradient(
    listOf(Color(0xFFFFF3D8), Color(0xFFFFE5F3), Color(0xFFE7E5FF))
)

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

    Box(
        modifier = Modifier.fillMaxSize().background(FUNDO_LOGIN).imePadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 440.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = ROSA)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎮", style = MaterialTheme.typography.displayMedium)
                    Text(
                        "Memória Play",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ROXO_ESCURO,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "🌟 Treine sua memória e divirta-se!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ROXO,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 440.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "🧩 Bem-vindo!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ROXO_ESCURO
                    )
                    Text(
                        "Entre na sua conta para começar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF655473)
                    )
                    OutlinedTextField(
                        value = uiState.usuario,
                        onValueChange = viewModel::onUsuarioChange,
                        label = { Text("Usuário") },
                        placeholder = { Text("Digite seu usuário") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ROXO,
                            focusedLabelColor = ROXO,
                            cursorColor = ROXO
                        )
                    )
                    OutlinedTextField(
                        value = uiState.senha,
                        onValueChange = viewModel::onSenhaChange,
                        label = { Text("Senha") },
                        placeholder = { Text("Digite sua senha") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ROXO,
                            focusedLabelColor = ROXO,
                            cursorColor = ROXO
                        )
                    )

                    uiState.mensagemErro?.let { erro ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFE4E4),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "⚠️ $erro",
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFF9D2020)
                            )
                        }
                    }

                    uiState.perfilLogado?.let { perfil ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    uiState.mensagemSucesso ?: "Logado como ${perfil.nome}",
                                    color = Color(0xFF245C2A),
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Perfil: ${perfil.descricaoTipo}")
                                TextButton(onClick = viewModel::onSair) {
                                    Text("Sair", color = ROXO)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = viewModel::onEntrar,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ROXO,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Entrar", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "✨ Sua aventura começa aqui!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = ROXO
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 440.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AMARELO)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔑 Acessos de demonstração", color = ROXO_ESCURO,
                        fontWeight = FontWeight.Bold)
                    Text("Administrador: admin / 1234", color = ROXO_ESCURO)
                    Text("Jogador: jogador / 1234", color = ROXO_ESCURO)
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("⭐ Memória Play ⭐", color = ROXO, fontWeight = FontWeight.Bold)
        }
    }
}
