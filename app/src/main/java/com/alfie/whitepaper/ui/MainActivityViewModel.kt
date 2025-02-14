package com.alfie.whitepaper.ui

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfie.whitepaper.data.constants.BY_SYSTEM
import com.alfie.whitepaper.ui.screen.home.usecases.GetAppPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getAppPreferenceUseCase: GetAppPreferenceUseCase
) : ViewModel() {

    val dynamicThemeState = mutableStateOf(false)
    val darkThemeState = mutableIntStateOf(BY_SYSTEM)

    init {
        getPreferences()
    }

    private fun getPreferences() {
        viewModelScope.launch {
            getAppPreferenceUseCase.invoke().collect {
                darkThemeState.intValue = it.isDarkTheme
                dynamicThemeState.value = it.isDynamicTheme
            }
        }
    }
}