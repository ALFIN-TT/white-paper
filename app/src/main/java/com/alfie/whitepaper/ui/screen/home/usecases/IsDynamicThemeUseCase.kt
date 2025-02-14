package com.alfie.whitepaper.ui.screen.home.usecases

import kotlinx.coroutines.flow.Flow

interface IsDynamicThemeUseCase {
    suspend operator fun invoke(): Flow<Boolean>

}