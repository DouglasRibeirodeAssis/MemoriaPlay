package br.com.memoriaplay.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.memoriaplay.features.home.viewmodel.HomeViewModel

/** Factory simples para os ViewModels utilizados pela navegação. */
class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(container.homeRepository) as T
        }
        throw IllegalArgumentException("ViewModel não suportado: ${modelClass.name}")
    }
}
