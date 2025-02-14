package com.alfie.whitepaper.ui.screen.home.usecases

import kotlinx.coroutines.flow.Flow

interface IsDarkThemeUseCase {
    suspend operator fun invoke(): Flow<Int>

}