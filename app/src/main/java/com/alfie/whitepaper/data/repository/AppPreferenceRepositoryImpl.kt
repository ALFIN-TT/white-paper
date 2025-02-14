package com.alfie.whitepaper.data.repository

import com.alfie.whitepaper.data.database.AppPreferenceDataStore
import com.alfie.whitepaper.data.model.AppPreference
import kotlinx.coroutines.flow.Flow

class AppPreferenceRepositoryImpl(
    private val appPreferenceDataStore: AppPreferenceDataStore
) : AppPreferenceRepository {
    override suspend fun setDarkThemeStatus(option: Int) {
        appPreferenceDataStore.setDarkThemeStatus(option)
    }

    override fun isDarkTheme(): Flow<Int> = appPreferenceDataStore.isDarkTheme()

    override suspend fun setDynamicThemeStatus(isDynamicTheme: Boolean) {
        appPreferenceDataStore.setDynamicThemeStatus(isDynamicTheme)
    }

    override fun isDynamicTheme(): Flow<Boolean> = appPreferenceDataStore.isDynamicTheme()

    override fun getAppPreferences(): Flow<AppPreference> =
        appPreferenceDataStore.getAppPreferences()
}