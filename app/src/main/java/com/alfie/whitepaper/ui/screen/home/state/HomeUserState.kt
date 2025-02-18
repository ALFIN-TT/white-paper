package com.alfie.whitepaper.ui.screen.home.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.data.constants.DarkThemeOption
import com.alfie.whitepaper.data.database.room.Project
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


data class InitialHomeUserState(
    var projectFilePathFromDeepLink: String = "",
    var isDeeplinkLoadCompleted: Boolean = false
)

data class HomeUIState(
    val projects: ImmutableList<DrawCanvasPayLoad> = persistentListOf(),
    val initialState: InitialHomeUserState = InitialHomeUserState(),
    val isEnableDynamicTheme: MutableState<Boolean> = mutableStateOf(false),
    val isEnableDarkTheme: MutableState<Int> = mutableIntStateOf(BY_SYSTEM),
    val isLoading: Boolean = false
)

sealed interface HomeEvent {
    data class DeleteProject(val id: String) : HomeEvent
    data class DarkThemeOptionName(@DarkThemeOption val optionType: Int) : HomeEvent
    data class SetDynamicTheme(val isEnable: Boolean): HomeEvent
    data class SetDarkTheme(@DarkThemeOption val option: Int): HomeEvent
}


