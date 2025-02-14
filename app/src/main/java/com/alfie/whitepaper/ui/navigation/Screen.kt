package com.alfie.whitepaper.ui.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home_screen")
    data object CanvasScreen : Screen("canvas_screen")
}