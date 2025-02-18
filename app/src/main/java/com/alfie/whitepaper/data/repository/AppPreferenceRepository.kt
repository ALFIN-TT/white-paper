package com.alfie.whitepaper.data.repository

import com.alfie.whitepaper.data.constants.DarkThemeOption
import com.alfie.whitepaper.data.model.AppPreference
import kotlinx.coroutines.flow.Flow

interface AppPreferenceRepository {
    suspend fun setDarkThemeStatus(@DarkThemeOption option: Int)
    fun isDarkTheme(): Flow<Int>
    suspend fun setDynamicThemeStatus(isDynamicTheme: Boolean)
    fun isDynamicTheme(): Flow<Boolean>
    fun getAppPreferences(): Flow<AppPreference>
    suspend fun updateAdFreeCount(adFreeCount: Int)
}