package br.com.memoriaplay.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.memoriaplay.features.home.viewmodel.HomeViewModel
import br.com.memoriaplay.features.config.viewmodel.ConfiguracoesViewModel
import br.com.memoriaplay.features.config.viewmodel.ConfiguracoesViewModelFactory

/** Factory simples para os ViewModels utilizados pela navegação. */
class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(container.homeRepository) as T
        }
        if (modelClass.isAssignableFrom(ConfiguracoesViewModel::class.java)) {
            return ConfiguracoesViewModelFactory(container.preferenciasRepository).create(modelClass)
        }
        throw IllegalArgumentException("ViewModel não suportado: ${modelClass.name}")
    }
}
