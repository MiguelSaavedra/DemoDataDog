package com.example.demodatadog.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.demodatadog.data.ApiFactory
import com.example.demodatadog.data.PokemonDetailResponse
import com.example.demodatadog.monitoring.DatadogAttributes
import com.example.demodatadog.monitoring.DatadogEvent
import com.example.demodatadog.monitoring.DatadogTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PokemonDetailUiState(
    val isLoading: Boolean = true,
    val pokemon: PokemonDetailResponse? = null,
    val error: String? = null,
)

class PokemonDetailViewModel(
    private val pokemonName: String,
) : ViewModel() {

    private val api = ApiFactory.pokeApi
    private val _uiState = MutableStateFlow(PokemonDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val detail = api.getPokemon(pokemonName)
                DatadogTracker.track(
                    DatadogEvent.POKEMON_DETAIL_LOADED,
                    mapOf(
                        DatadogAttributes.POKEMON_NAME to detail.name,
                        DatadogAttributes.POKEMON_ID to detail.id,
                    ),
                )
                _uiState.update { it.copy(isLoading = false, pokemon = detail, error = null) }
            } catch (error: Exception) {
                DatadogTracker.logError("Error al cargar el detalle", error)
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "Error al cargar detalle")
                }
            }
        }
    }

    companion object {
        fun factory(name: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PokemonDetailViewModel(name) as T
                }
            }
    }
}
