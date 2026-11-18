package br.com.memoriaplay.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import br.com.memoriaplay.di.AppContainer
import br.com.memoriaplay.di.AppViewModelFactory
import br.com.memoriaplay.navigation.NavegacaoApp

/**
 * Raiz de composição do Memória Play.
 * O container é criado uma única vez por instância da aplicação e compartilhado
 * com a navegação e com os ViewModels.
 */
@Composable
fun MemoriaPlayApp() {
    val context = LocalContext.current
    val container = remember(context) { AppContainer(context.applicationContext) }
    val factory = remember(container) { AppViewModelFactory(container) }
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavegacaoApp(viewModelFactory = factory)
        }
    }
}
