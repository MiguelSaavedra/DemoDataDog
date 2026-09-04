package com.example.demodatadog.monitoring

import androidx.navigation.NavDestination
import com.datadog.android.rum.tracking.ComponentPredicate

class DatadogNavDestinationPredicate : ComponentPredicate<NavDestination> {
    override fun accept(component: NavDestination): Boolean = true

    override fun getViewName(component: NavDestination): String? {
        val route = component.route.orEmpty()
        return when {
            route.startsWith("list") -> "PokemonList"
            route.startsWith("detail") -> "PokemonDetail"
            else -> route.substringBefore('/').ifBlank { null }
        }
    }
}
