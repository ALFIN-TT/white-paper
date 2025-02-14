package com.alfie.whitepaper.ui.screen.home.usecases

import com.alfie.whitepaper.data.repository.AppPreferenceRepository
import kotlinx.coroutines.flow.Flow

class IsDynamicThemeUseCaseImpl(
    private val repository: AppPreferenceRepository
) : IsDynamicThemeUseCase {
    override suspend fun invoke(): Flow<Boolean> = repository.isDynamicTheme()

}