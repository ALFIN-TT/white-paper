package com.alfie.whitepaper.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

// DataStore Instance
val Context._dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Preference Keys
val themePreferenceKey = intPreferencesKey("list_theme")


// Retrieving functions
/**
 * extension [isDarkThemeOn] checks the saved theme from preference
 * and returns boolean
 */
fun Context.isDarkThemeOn() = _dataStore.data
    .map { preferences ->
        // No type safety.
        preferences[themePreferenceKey] ?: 0
    }

