package br.com.memoriaplay.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.memoriaplay.di.AppContainer
import br.com.memoriaplay.di.AppViewModelFactory
import br.com.memoriaplay.navigation.NavegacaoApp
import br.com.memoriaplay.ui.theme.MemoriaPlayTheme

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
    val preferencias by container.preferenciasRepository.preferencias.collectAsStateWithLifecycle()
    MemoriaPlayTheme(preferencias = preferencias) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NavegacaoApp(viewModelFactory = factory)
        }
    }
}
