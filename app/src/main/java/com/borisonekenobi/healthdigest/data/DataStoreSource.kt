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
import com.borisonekenobi.healthdigest.model.settings.GoalPreferences
import com.borisonekenobi.healthdigest.model.settings.Range
import com.borisonekenobi.healthdigest.model.settings.PersonalInformation
import com.borisonekenobi.healthdigest.model.settings.ReportPreferences
import com.borisonekenobi.healthdigest.model.settings.SystemPreferences
import com.borisonekenobi.healthdigest.model.settings.Theme
import com.borisonekenobi.healthdigest.model.settings.Units
import com.borisonekenobi.healthdigest.model.settings.UserPreferences
import com.borisonekenobi.healthdigest.model.settings.WeightGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreSource(private val context: Context) {
    val personalInformationFlow: Flow<PersonalInformation> = context.dataStore.data.map { preferences ->
        PersonalInformation(
            sex = getEnum(preferences, PreferenceKeys.SEX, null),
            birthDate = getBirthDate(preferences),
        )
    }

    val goalPreferencesFlow: Flow<GoalPreferences> = context.dataStore.data.map { preferences ->
        GoalPreferences(
            weightGoal = getEnum(preferences, PreferenceKeys.WEIGHT_GOAL, WeightGoal.MAINTAIN)!!,
            autoNutritionGoals = getBoolean(preferences, PreferenceKeys.AUTO_NUTRITION_GOALS),
            calorieGoal = Range(
                lowerBound = getEnergy(preferences, PreferenceKeys.CALORIE_GOAL_LOWER),
                upperBound = getEnergy(preferences, PreferenceKeys.CALORIE_GOAL_UPPER)
            ),
            proteinGoal = Range(
                lowerBound = getMass(preferences, PreferenceKeys.PROTEIN_GOAL_LOWER),
                upperBound = getMass(preferences, PreferenceKeys.PROTEIN_GOAL_UPPER)
            ),
            carbsGoal = Range(
                lowerBound = getMass(preferences, PreferenceKeys.CARBS_GOAL_LOWER),
                upperBound = getMass(preferences, PreferenceKeys.CARBS_GOAL_UPPER)
            ),
            fatGoal = Range(
                lowerBound = getMass(preferences, PreferenceKeys.FAT_GOAL_LOWER),
                upperBound = getMass(preferences, PreferenceKeys.FAT_GOAL_UPPER)
            ),
            autoHydrationGoals = getBoolean(preferences, PreferenceKeys.AUTO_HYDRATION_GOALS),
            waterGoal = getVolume(preferences, PreferenceKeys.WATER_GOAL)
        )
    }

    val reportPreferencesFlow: Flow<ReportPreferences> = context.dataStore.data.map { preferences ->
        ReportPreferences(
            startMessage = getString(preferences, PreferenceKeys.START_MESSAGE),
            endMessage = getString(preferences, PreferenceKeys.END_MESSAGE)
        )
    }

    val systemPreferencesFlow: Flow<SystemPreferences> = context.dataStore.data.map { preferences ->
        SystemPreferences(
            theme = getEnum(preferences, PreferenceKeys.THEME, Theme.SYSTEM)!!,
            units = getEnum(preferences, PreferenceKeys.UNITS, Units.METRIC)!!
        )
    }

    val userPreferencesFlow: Flow<UserPreferences> = combine(
        personalInformationFlow,
        goalPreferencesFlow,
        reportPreferencesFlow,
        systemPreferencesFlow
    ) { personalInformation, goalPreferences, reportPreferences, systemPreferences ->
        UserPreferences(
            personalInformation,
            goalPreferences,
            reportPreferences,
            systemPreferences,
        )
    }

    suspend fun saveUserPreference(key: PreferenceKeys, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key.value] = value
        }
    }

    private inline fun <reified T : Enum<T>> getEnum(preferences: Preferences, key: PreferenceKeys, default: T? = null): T? = try {
        enumValueOf(preferences[key.value] ?: "")
    } catch (_: Exception) {
        default
    }

    private fun getBirthDate(preferences: Preferences): LocalDate? = try {
        LocalDate.parse(preferences[PreferenceKeys.BIRTH_DATE.value])
    } catch (_: Exception) {
        null
    }

    private fun getBoolean(preferences: Preferences, key: PreferenceKeys): Boolean = try {
        preferences[key.value].toBoolean()
    } catch (_: Exception) {
        false
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
    SEX(stringPreferencesKey("sex")),
    BIRTH_DATE(stringPreferencesKey("birth-date")),

    THEME(stringPreferencesKey("theme")),
    UNITS(stringPreferencesKey("units")),

    WEIGHT_GOAL(stringPreferencesKey("weight-goal")),
    AUTO_NUTRITION_GOALS(stringPreferencesKey("auto-nutrition-goals")),
    CALORIE_GOAL_LOWER(stringPreferencesKey("calorie-goal-lower")),
    CALORIE_GOAL_UPPER(stringPreferencesKey("calorie-goal-upper")),
    PROTEIN_GOAL_LOWER(stringPreferencesKey("protein-goal-lower")),
    PROTEIN_GOAL_UPPER(stringPreferencesKey("protein-goal-upper")),
    CARBS_GOAL_LOWER(stringPreferencesKey("carbs-goal-lower")),
    CARBS_GOAL_UPPER(stringPreferencesKey("carbs-goal-upper")),
    FAT_GOAL_LOWER(stringPreferencesKey("fat-goal-lower")),
    FAT_GOAL_UPPER(stringPreferencesKey("fat-goal-upper")),
    AUTO_HYDRATION_GOALS(stringPreferencesKey("auto-hydration-goals")),
    WATER_GOAL(stringPreferencesKey("water-goal")),

    START_MESSAGE(stringPreferencesKey("start-message")),
    END_MESSAGE(stringPreferencesKey("end-message")),
}
