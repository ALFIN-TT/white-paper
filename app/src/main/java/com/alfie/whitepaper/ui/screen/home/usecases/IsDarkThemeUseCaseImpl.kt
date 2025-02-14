package com.alfie.whitepaper.ui.screen.home.usecases

import com.alfie.whitepaper.data.repository.AppPreferenceRepository
import kotlinx.coroutines.flow.Flow

class IsDarkThemeUseCaseImpl(
    private val repository: AppPreferenceRepository
) : IsDarkThemeUseCase {
    override suspend fun invoke(): Flow<Int> = repository.isDarkTheme()

}