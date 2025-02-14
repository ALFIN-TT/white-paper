package com.alfie.whitepaper.ui.screen.home.usecases

interface SetDynamicThemeStatusUseCase {
    suspend operator fun invoke(isDynamicTheme: Boolean)

}