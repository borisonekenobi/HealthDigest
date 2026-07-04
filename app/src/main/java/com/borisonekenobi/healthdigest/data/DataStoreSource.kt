package com.borisonekenobi.healthdigest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Volume
import com.borisonekenobi.healthdigest.model.Range
import com.borisonekenobi.healthdigest.model.Goals
import com.borisonekenobi.healthdigest.model.Messages
import com.borisonekenobi.healthdigest.model.Theme
import com.borisonekenobi.healthdigest.model.Units
import com.borisonekenobi.healthdigest.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreSource(private val context: Context) {
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            getTheme(preferences),
            getUnits(preferences),
            Goals(
                Range(
                    getEnergy(preferences, PreferenceKeys.CALORIE_GOAL_LOWER),
                    getEnergy(preferences, PreferenceKeys.CALORIE_GOAL_UPPER)
                ), Range(
                    getMass(preferences, PreferenceKeys.PROTEIN_GOAL_LOWER),
                    getMass(preferences, PreferenceKeys.PROTEIN_GOAL_UPPER)
                ), Range(
                    getMass(preferences, PreferenceKeys.CARBS_GOAL_LOWER),
                    getMass(preferences, PreferenceKeys.CARBS_GOAL_UPPER)
                ), Range(
                    getMass(preferences, PreferenceKeys.FAT_GOAL_LOWER),
                    getMass(preferences, PreferenceKeys.FAT_GOAL_UPPER)
                ), getVolume(preferences, PreferenceKeys.WATER_GOAL)
            ),
            Messages(
                getString(preferences, PreferenceKeys.START_MESSAGE),
                getString(preferences, PreferenceKeys.END_MESSAGE)
            ),
        )
    }

    suspend fun saveUserPreference(key: PreferenceKeys, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key.value] = value
        }
    }

    private fun getTheme(preferences: Preferences): Theme = try {
        Theme.valueOf(preferences[PreferenceKeys.THEME.value] ?: Theme.SYSTEM.name)
    } catch (_: Exception) {
        Theme.SYSTEM
    }

    private fun getUnits(preferences: Preferences): Units = try {
        Units.valueOf(preferences[PreferenceKeys.UNITS.value] ?: Units.METRIC.name)
    } catch (_: Exception) {
        Units.METRIC
    }

    private fun getEnergy(preferences: Preferences, key: PreferenceKeys): Energy? = try {
        Energy.kilocalories((preferences[key.value] ?: "").toDouble())
    } catch (_: Exception) {
        null
    }

    private fun getMass(preferences: Preferences, key: PreferenceKeys): Mass? = try {
        Mass.grams((preferences[key.value] ?: "").toDouble())
    } catch (_: Exception) {
        null
    }

    private fun getVolume(preferences: Preferences, key: PreferenceKeys): Volume? = try {
        Volume.milliliters((preferences[key.value] ?: "").toDouble())
    } catch (_: Exception) {
        null
    }

    private fun getString(preferences: Preferences, key: PreferenceKeys): String = try {
        preferences[key.value] ?: ""
    } catch (_: Exception) {
        ""
    }
}

enum class PreferenceKeys(val value: Preferences.Key<String>) {
    THEME(stringPreferencesKey("theme")),
    UNITS(stringPreferencesKey("units")),
    CALORIE_GOAL_LOWER(stringPreferencesKey("calorie-goal-lower")),
    CALORIE_GOAL_UPPER(stringPreferencesKey("calorie-goal-upper")),
    PROTEIN_GOAL_LOWER(stringPreferencesKey("protein-goal-lower")),
    PROTEIN_GOAL_UPPER(stringPreferencesKey("protein-goal-upper")),
    CARBS_GOAL_LOWER(stringPreferencesKey("carbs-goal-lower")),
    CARBS_GOAL_UPPER(stringPreferencesKey("carbs-goal-upper")),
    FAT_GOAL_LOWER(stringPreferencesKey("fat-goal-lower")),
    FAT_GOAL_UPPER(stringPreferencesKey("fat-goal-upper")),
    WATER_GOAL(stringPreferencesKey("water-goal")),
    START_MESSAGE(stringPreferencesKey("start-message")),
    END_MESSAGE(stringPreferencesKey("end-message")),
}
