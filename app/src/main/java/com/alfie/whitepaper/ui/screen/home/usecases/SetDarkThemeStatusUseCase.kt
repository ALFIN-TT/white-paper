package com.alfie.whitepaper.ui.screen.home.usecases

import com.alfie.whitepaper.data.constants.DarkThemeOption

interface SetDarkThemeStatusUseCase {
    suspend operator fun invoke(@DarkThemeOption darkThemeOption: Int)

}