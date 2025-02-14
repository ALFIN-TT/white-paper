package com.alfie.whitepaper.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alfie.whitepaper.data.constants.DarkThemeOption
import com.alfie.whitepaper.data.constants.LIGHT_MODE
import com.alfie.whitepaper.data.model.AppPreference
import kotlinx.coroutines.flow.map

const val APP_PREFERENCE_DATASTORE = "app_preference_datastore"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCE_DATASTORE)

class AppPreferenceDataStore(val context: Context) {
    companion object {
        val DARK_THEME = intPreferencesKey("dark_theme")
        val DYNAMIC_THEME = booleanPreferencesKey("dynamic_theme")
    }

    suspend fun setDarkThemeStatus(@DarkThemeOption option: Int) {
        context.dataStore.edit {
            it[DARK_THEME] = option
        }
    }

    fun isDarkTheme() = context.dataStore.data.map {
        it[DARK_THEME] ?: LIGHT_MODE
    }

    suspend fun setDynamicThemeStatus(isDynamicTheme: Boolean) {
        context.dataStore.edit {
            it[DYNAMIC_THEME] = isDynamicTheme
        }
    }

    fun isDynamicTheme() = context.dataStore.data.map {
        it[DYNAMIC_THEME] ?: false
    }

    fun getAppPreferences() = context.dataStore.data.map {
        AppPreference(
            it[DYNAMIC_THEME] ?: false,
            it[DARK_THEME] ?: LIGHT_MODE
        )
    }


    suspend fun clearAppPreference() = context.dataStore.edit {
        it.clear()
    }
}