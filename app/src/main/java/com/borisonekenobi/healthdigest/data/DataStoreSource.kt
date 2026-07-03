package com.borisonekenobi.healthdigest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.borisonekenobi.healthdigest.model.Theme
import com.borisonekenobi.healthdigest.model.Units
import com.borisonekenobi.healthdigest.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreSource(private val context: Context) {
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            val units = try {
                Units.valueOf(preferences[PreferenceKeys.UNITS.value] ?: Units.METRIC.name)
            } catch (_: Exception) {
                Units.METRIC
            }

            val startMessage = try {
                preferences[PreferenceKeys.START_MESSAGE.value] ?: ""
            } catch (_: Exception) {
                ""
            }

            val endMessage = try {
                preferences[PreferenceKeys.END_MESSAGE.value] ?: ""
            } catch (_: Exception) {
                ""
            }

            val theme = try {
                Theme.valueOf(preferences[PreferenceKeys.THEME.value] ?: Theme.SYSTEM.name)
            } catch (_: Exception) {
                Theme.SYSTEM
            }

            UserPreferences(units, startMessage, endMessage, theme)
        }

    suspend fun saveUserPreference(key: PreferenceKeys, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key.value] = value
        }
    }
}

enum class PreferenceKeys(val value: Preferences.Key<String>) {
    UNITS(stringPreferencesKey("units")),
    START_MESSAGE(stringPreferencesKey("start-message")),
    END_MESSAGE(stringPreferencesKey("end-message")),
    THEME(stringPreferencesKey("theme")),
}
