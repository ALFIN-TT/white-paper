package com.alfie.whitepaper.ui.screen.home.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.alfie.basicnetworkapplication.ui.screens.core.BaseState
import com.alfie.whitepaper.R
import com.alfie.whitepaper.core.ui.core.StateHolder
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.data.constants.DarkThemeOption
import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad

class HomeUserState {
    var initialState: InitialHomeUserState = InitialHomeUserState()
    var getProjectsLoader: MutableState<Boolean> = mutableStateOf(false)
    var isEnableDynamicTheme: MutableState<Boolean> = mutableStateOf(false)
    var isEnableDarkTheme: MutableState<Int> = mutableIntStateOf(BY_SYSTEM)
    var homeState: StateHolder<HomeState> = StateHolder(HomeState())
    var getDarkThemeOptionName: (@DarkThemeOption Int) -> Int = { _ -> R.string.str_empty }
}

data class InitialHomeUserState(
    var projectFilePathFromDeepLink: String = "",
    var isDeeplinkLoadCompleted: Boolean = false
)

data class HomeState(
    var projects: List<DrawCanvasPayLoad> = listOf()
) : BaseState()


/**
 * Home Event handler.
 */
data class HomeUserEvents(
    var onDelete: (String, (() -> Unit)) -> Unit = { _, _ -> },
    var onDynamicThemeChange: (Boolean) -> Unit = {},
    var onDarkThemeChange: (@DarkThemeOption Int) -> Unit = {},
    var onImportProject: (String, ((Boolean, Project?) -> Unit)) -> Unit = { _, _ -> }
)
