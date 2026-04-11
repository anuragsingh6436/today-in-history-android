package com.bajrangi.todayinhistory.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bajrangi.todayinhistory.presentation.detail.DetailScreen
import com.bajrangi.todayinhistory.presentation.home.HomeScreen
import com.bajrangi.todayinhistory.presentation.home.HomeViewModel
import com.bajrangi.todayinhistory.presentation.settings.SettingsScreen
import com.bajrangi.todayinhistory.presentation.settings.SettingsViewModel

@Composable
fun HistoryNavGraph() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(400)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onEventClick = { index ->
                    navController.navigate(Screen.Detail.createRoute(index))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("eventIndex") { type = NavType.IntType }
            ),
        ) { backStackEntry ->
            val eventIndex = backStackEntry.arguments?.getInt("eventIndex") ?: 0
            val event = homeViewModel.getEvent(eventIndex)

            DetailScreen(
                event = event,
                onBack = { navController.popBackStack() },
            )
        }

        composable(route = Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
