package com.bajrangi.todayinhistory.presentation.navigation

/**
 * All navigation destinations in the app.
 *
 * Keeping routes as sealed objects makes them type-safe and
 * refactor-friendly — no magic strings scattered across the codebase.
 */
sealed class Screen(val route: String) {
    /** Today's events feed — the default landing screen. */
    data object Home : Screen("home")

    /**
     * Full event detail with AI summary.
     *
     * Receives the event index within the current list so the
     * ViewModel can look it up without re-fetching.
     */
    data object Detail : Screen("detail/{eventIndex}") {
        fun createRoute(eventIndex: Int) = "detail/$eventIndex"
    }
}
