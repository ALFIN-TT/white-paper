package com.alfie.whitepaper.ui.navigation

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.alfie.whitepaper.data.constants.Keys
import com.alfie.whitepaper.ui.screen.canvas.presentation.CanvasUI
import com.alfie.whitepaper.ui.screen.canvas.transformations.CanvasUIViewModelEventTransform
import com.alfie.whitepaper.ui.screen.canvas.transformations.CanvasUIViewModelStateTransform
import com.alfie.whitepaper.ui.screen.canvas.viewmodel.CanvasUIViewModel
import com.alfie.whitepaper.ui.screen.home.presentation.HomeUI
import com.alfie.whitepaper.ui.screen.home.transformations.HomeUIViewModelEventTransform
import com.alfie.whitepaper.ui.screen.home.transformations.HomeUIViewModelStateTransform
import com.alfie.whitepaper.ui.screen.home.viewmodel.HomeUIViewModel

@Composable
fun ScreenNavigation(
    isEnabledDynamicTheme: MutableState<Boolean>,
    isEnabledDarkTheme: MutableState<Int>,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(
            route = Screen.HomeScreen.route.plus("?project={projectPath}"),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "content://{project}"
                    mimeType = "text/plain"
                }),
            arguments = listOf(
                navArgument("project") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            var projectFromDeepLink: Intent? = null
            if (it.arguments?.containsKey(Keys.KEY_RESULT_INTENT) == true) {
                projectFromDeepLink = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.arguments?.getParcelable(
                        Keys.KEY_RESULT_INTENT,
                        Intent::class.java
                    )
                } else it.arguments?.getParcelable(Keys.KEY_RESULT_INTENT)
                it.arguments?.remove(Keys.KEY_RESULT_INTENT)
            }
            NavigateToHomeUI(
                projectFromDeepLink = (projectFromDeepLink?.data ?: "").toString(),
                isEnabledDynamicTheme = isEnabledDynamicTheme,
                isEnabledDarkTheme = isEnabledDarkTheme,
                navController = navController
            )
        }
        composable(
            route = Screen.CanvasScreen.route.plus("?${Keys.PROJECT_NAME}={${Keys.KEY_PROJECT_NAME}}"),
            arguments = listOf(
                navArgument(Keys.KEY_PROJECT_NAME) {
                    type = NavType.StringType
                }
            )
        ) {
            NavigateToCanvasUI(navController)
        }
    }
}


@Composable
private fun NavigateToHomeUI(
    projectFromDeepLink: String,
    isEnabledDynamicTheme: MutableState<Boolean>,
    isEnabledDarkTheme: MutableState<Int>,
    navController: NavController
) {
    val viewModel = hiltViewModel<HomeUIViewModel>()
    val userState = HomeUIViewModelStateTransform().transform(viewModel)
    userState.apply {
        isEnableDynamicTheme = isEnabledDynamicTheme
        isEnableDarkTheme = isEnabledDarkTheme
        initialState.projectFilePathFromDeepLink = projectFromDeepLink
    }
    val userEvent = HomeUIViewModelEventTransform().transform(viewModel)
    HomeUI(
        navController = navController,
        userState = userState,
        userEvent = userEvent
    )
}

@Composable
private fun NavigateToCanvasUI(navController: NavController) {
    val viewModel = hiltViewModel<CanvasUIViewModel>()
    val userState = CanvasUIViewModelStateTransform().transform(viewModel)
    val userEvent = CanvasUIViewModelEventTransform().transform(viewModel)
    CanvasUI(
        navController = navController,
        userState = userState,
        userEvent = userEvent,
    )
}
