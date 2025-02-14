package com.alfie.whitepaper.ui.screen.home.usecases

import com.alfie.whitepaper.data.repository.AppPreferenceRepository

class SetDynamicThemeStatusUseCaseImpl(
    private val repository: AppPreferenceRepository
) : SetDynamicThemeStatusUseCase {
    override suspend fun invoke(isDynamicTheme: Boolean) {
        repository.setDynamicThemeStatus(isDynamicTheme)
    }
}