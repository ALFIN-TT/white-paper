package com.alfie.whitepaper.data.model

import com.alfie.whitepaper.data.constants.DarkThemeOption

data class AppPreference(
    val isDynamicTheme: Boolean,
    @DarkThemeOption val isDarkTheme: Int,
    val adFreeCount: Int = 0
)