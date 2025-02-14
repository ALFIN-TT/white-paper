package com.alfie.whitepaper.ui.screen.home.usecases

import com.alfie.whitepaper.data.repository.AppPreferenceRepository

class SetDarkThemeStatusUseCaseImpl(
    private val repository: AppPreferenceRepository
) : SetDarkThemeStatusUseCase {
    override suspend fun invoke(darkThemeOption: Int) {
        repository.setDarkThemeStatus(darkThemeOption)
    }

}