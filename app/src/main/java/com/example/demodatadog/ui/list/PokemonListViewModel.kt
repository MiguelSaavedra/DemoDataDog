package com.example.demodatadog.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demodatadog.data.ApiFactory
import com.example.demodatadog.data.PokemonListItem
import com.example.demodatadog.monitoring.DatadogAttributes
import com.example.demodatadog.monitoring.DatadogEvent
import com.example.demodatadog.monitoring.DatadogTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PokemonListUiState(
    val isLoading: Boolean = false,
    val pokemon: List<PokemonListItem> = emptyList(),
    val error: String? = null,
)

class PokemonListViewModel : ViewModel() {

    private val api = ApiFactory.pokeApi
    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPokemon()
    }

    fun loadPokemon() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = api.getPokemonList(limit = 20, offset = 0)
                DatadogTracker.track(
                    DatadogEvent.POKEMON_LIST_LOADED,
                    mapOf(DatadogAttributes.POKEMON_COUNT to response.results.size),
                )
                _uiState.update {
                    it.copy(isLoading = false, pokemon = response.results, error = null)
                }
            } catch (error: Exception) {
                DatadogTracker.logError("Error al cargar la lista", error)
                _uiState.update {
                    it.copy(isLoading = false, error = error.message ?: "Error al cargar")
                }
            }
        }
    }

    fun onPokemonSelected(name: String) {
        DatadogTracker.track(
            DatadogEvent.POKEMON_SELECTED,
            mapOf(DatadogAttributes.POKEMON_NAME to name),
        )
    }

    fun simulateError() {
        val error = IllegalStateException("Error de demo simulado")
        DatadogTracker.logError(DatadogEvent.SIMULATED_ERROR, error)
        _uiState.update { it.copy(error = error.message) }
    }
}
