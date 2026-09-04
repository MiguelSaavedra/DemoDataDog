package com.example.demodatadog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.datadog.android.compose.NavigationViewTrackingEffect
import com.example.demodatadog.monitoring.DatadogNavDestinationPredicate
import com.example.demodatadog.ui.detail.PokemonDetailScreen
import com.example.demodatadog.ui.list.PokemonListScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()
    NavigationViewTrackingEffect(
        navController = navController,
        trackArguments = false,
        destinationPredicate = DatadogNavDestinationPredicate(),
    )
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            PokemonListScreen(
                onPokemonClick = { name -> navController.navigate("detail/$name") },
            )
        }
        composable(
            route = "detail/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType }),
        ) { entry ->
            val name = entry.arguments?.getString("name").orEmpty()
            PokemonDetailScreen(
                name = name,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
