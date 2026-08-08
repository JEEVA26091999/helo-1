package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        val SELECTED_LANGUAGE_KEY = stringPreferencesKey("selected_language")
    }

    val selectedLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_LANGUAGE_KEY] ?: "All"
        }

    suspend fun saveSelectedLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE_KEY] = language
        }
    }
}
