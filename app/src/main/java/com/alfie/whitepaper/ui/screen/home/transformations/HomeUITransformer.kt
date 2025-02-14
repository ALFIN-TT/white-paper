package com.alfie.whitepaper.ui.screen.home.transformations

import com.alfie.basicnetworkapplication.ui.screens.core.Transformer
import com.alfie.whitepaper.ui.screen.home.state.HomeUserEvents
import com.alfie.whitepaper.ui.screen.home.state.HomeUserState
import com.alfie.whitepaper.ui.screen.home.viewmodel.HomeUIViewModel

class HomeUIViewModelStateTransform : Transformer<HomeUIViewModel, HomeUserState> {
    override fun transform(input: HomeUIViewModel) = with(input) {
        val userState = HomeUserState()
        userState.homeState = input.homeState.value
        userState.initialState = input.projectPathFromDeepLink
        userState.getDarkThemeOptionName = { darkThemeOption ->
            getDarkThemeOptionName(darkThemeOption)
        }
        userState
    }
}


class HomeUIViewModelEventTransform : Transformer<HomeUIViewModel, HomeUserEvents> {
    override fun transform(input: HomeUIViewModel) = with(input) {
        val userEvents = HomeUserEvents()
        userEvents.onDelete = { projectName, callback ->
            onDeleteProjectRequested(projectName, callback)
        }
        userEvents.onDarkThemeChange = { darkThemeOptions ->
            onDarkThemeStatusChangeRequested(darkThemeOptions)
        }
        userEvents.onDynamicThemeChange = { isDynamicTheme ->
            onDynamicThemeStatusChangeRequested(isDynamicTheme)
        }
        userEvents.onImportProject = { importedData, callback ->
            onImportProject(importedData, callback)
        }
        userEvents
    }
}
